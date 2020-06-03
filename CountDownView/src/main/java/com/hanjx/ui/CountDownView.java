package com.hanjx.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;
import com.hanjx.ui.countdownview.R;
import com.hanjx.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class CountDownView extends View {
    private static final int DEFAULT_LENGTH = SizeUtils.dp2px(60);
    private static final int DEFAULT_START_ANGLE = -90;
    private static final int DEFAULT_DURATION = 2000;
    private static final int DEFAULT_INTERVAL = 16;
    private static final int DEFAULT_FINISHED_COLOR = 0xFF3C7CFC;
    private static final int DEFAULT_UNFINISHED_COLOR = 0x00000000;
    private static final int DEFAULT_STROKE = SizeUtils.dp2px(3);
    private static final int DEFAULT_TEXT_COLOR = 0xFF3C7CFC;
    private static final int DEFAULT_TEXT_SIZE = SizeUtils.sp2px(15);
    private static final int MODE_FIXED = 0;
    private static final int MODE_TIME_VARIANT = 1;

    private float width;
    private float height;
    private float length;
    private int padding;

    // 是否顺时针旋转，默认true
    private boolean clockwise;
    // 初始角度，默认 -90 即顶部
    private float startAngle;
    // 完成的颜色
    private int finishedColor;
    // 未完成的颜色
    private int unfinishedColor;
    // 圆环宽度
    private float strokeWidth;

    private Paint circlePaint;
    private RectF oval = new RectF();

    // 文字内容
    private String text;
    // 文字显示模式
    private int textMode;
    // 文字颜色
    private int textColor;
    // 文字大小
    private int textSize;

    private Paint textPaint;
    private TextDrawer textDrawer;

    // 自动开始
    private boolean autoStart;
    // 总时间
    private int duration;
    // 剩余时间
    private int leftTime;
    // 刷新间隔
    private int interval;
    // 完成角度
    private int finishedAngle;

    private CountDownTimer timer;

    private CountDownListener listener;

    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
        duration = a.getInteger(R.styleable.CountDownView_duration, DEFAULT_DURATION);
        interval = a.getInteger(R.styleable.CountDownView_refresh_interval, DEFAULT_INTERVAL);
        finishedColor = a.getColor(R.styleable.CountDownView_finished_color, DEFAULT_FINISHED_COLOR);
        unfinishedColor = a.getColor(R.styleable.CountDownView_unfinished_color, DEFAULT_UNFINISHED_COLOR);
        strokeWidth = a.getDimensionPixelSize(R.styleable.CountDownView_paint_stroke, DEFAULT_STROKE);
        startAngle = a.getFloat(R.styleable.CountDownView_start_angle, DEFAULT_START_ANGLE);
        clockwise = a.getBoolean(R.styleable.CountDownView_clockwise, true);
        autoStart = a.getBoolean(R.styleable.CountDownView_auto_start, false);
        text = a.getString(R.styleable.CountDownView_text);
        if (text == null) {
            text = "";
        }
        textColor = a.getColor(R.styleable.CountDownView_text_color, DEFAULT_TEXT_COLOR);
        textSize = a.getDimensionPixelSize(R.styleable.CountDownView_text_size, DEFAULT_TEXT_SIZE);
        textMode = a.getInteger(R.styleable.CountDownView_text_mode, MODE_TIME_VARIANT);
        a.recycle();

        int[] paddingAttr = new int[] {
                android.R.attr.padding,
                android.R.attr.paddingLeft,
                android.R.attr.paddingTop,
                android.R.attr.paddingBottom,
                android.R.attr.paddingRight,
                android.R.attr.paddingStart,
                android.R.attr.paddingEnd
        };
        TypedArray pa = context.obtainStyledAttributes(attrs, paddingAttr);
        List<Integer> paddingList = new ArrayList<>();
        for (int i = 0; i < paddingAttr.length; i++) {
            paddingList.add(pa.getDimensionPixelSize(i, 0));
        }
        padding = MathUtils.max(paddingList);
        pa.recycle();

        initPaint();
        textDrawer = new TextDrawer() { };

        if (autoStart) {
            post(this::start);
        }
    }

    private void initPaint() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(strokeWidth);

        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
    }

    public void start() {
        start(duration);
    }

    public void start(int duration) {
        if (timer != null) {
            timer.cancel();
        }

        this.duration = duration;
        timer = new CountDownTimer(duration, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                leftTime = (int) millisUntilFinished;
                finishedAngle = Math.round(360 - 360f * millisUntilFinished / duration);
                invalidate();
                if (listener != null) {
                    listener.onTick(millisUntilFinished, finishedAngle);
                }
            }

            @Override
            public void onFinish() {
                leftTime = 0;
                finishedAngle = 360;
                invalidate();
                if (listener != null) {
                    listener.onFinished();
                }
            }
        };
        timer.start();
    }

    public void stop() {
        stop(true);
    }

    public void stop(boolean reset) {
        if (timer != null) {
            timer.cancel();
        }

        if (reset) {
            finishedAngle = 360;
            invalidate();
        }

        if (listener != null) {
            listener.onStop(reset);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 默认宽高 60dp
        int widthSpec = getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT ?
                MeasureSpec.makeMeasureSpec(DEFAULT_LENGTH, MeasureSpec.EXACTLY) : widthMeasureSpec;
        int heightSpec = getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT ?
                MeasureSpec.makeMeasureSpec(DEFAULT_LENGTH, MeasureSpec.EXACTLY) : heightMeasureSpec;

        width = MeasureSpec.getSize(widthSpec);
        height = MeasureSpec.getSize(heightSpec);
        length = Math.min(width, height);

        setMeasuredDimension(widthSpec, heightSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        length = Math.min(width, height);
        strokeWidth = Math.max(strokeWidth, length / 2f);

        float left = (width - length + strokeWidth) / 2f + padding;
        float right = (width + length - strokeWidth) / 2f - padding;
        float top = (height - length + strokeWidth) / 2f + padding;
        float bottom = (height + length - strokeWidth) / 2f - padding;
        oval.set(left, top, right, bottom);

        float finishedStart = clockwise ? startAngle : startAngle - finishedAngle;
        float unfinishedStart = clockwise ? startAngle + finishedAngle : startAngle;

        circlePaint.setColor(finishedColor);
        canvas.drawArc(oval, finishedStart, finishedAngle, false, circlePaint);

        circlePaint.setColor(unfinishedColor);
        canvas.drawArc(oval, unfinishedStart, 360 - finishedAngle, false, circlePaint);

        textDrawer.setTextPaint(textPaint, leftTime, textMode);
        text = textDrawer.getText(leftTime, textMode, text);
        float textWidth = textPaint.measureText(text);
        float textX = (left + right - textWidth) / 2;
        float textY = (top + bottom) / 2 + Math.abs(textPaint.descent() + textPaint.ascent()) / 2;
        canvas.drawText(text, textX, textY, textPaint);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        int newPadding = MathUtils.max(left, top, right, bottom);
        if (newPadding != padding) {
            padding = newPadding;
            invalidate();
        }
    }

    public CountDownView setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
        return this;
    }

    public CountDownView setPadding(int padding) {
        if (this.padding != padding) {
            this.padding = padding;
            invalidate();
        }
        return this;
    }

    public CountDownView setStartAngle(float startAngle) {
        this.startAngle = startAngle;
        return this;
    }

    public CountDownView setFinishedColor(int finishedColor) {
        this.finishedColor = finishedColor;
        return this;
    }

    public CountDownView setUnfinishedColor(int unfinishedColor) {
        this.unfinishedColor = unfinishedColor;
        return this;
    }

    public CountDownView setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    public CountDownView setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public CountDownView setInterval(int interval) {
        this.interval = interval;
        return this;
    }

    public CountDownView setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
        if (autoStart) {
            post(this::start);
        }
        return this;
    }

    public CountDownView setCountDownListener(CountDownListener listener) {
        this.listener = listener;
        return this;
    }

    public CountDownView setTextDrawer(TextDrawer textDrawer) {
        this.textDrawer = textDrawer;
        return this;
    }

    public interface CountDownListener {
        default void onTick(long leftTime, float finishedAngle) { }
        default void onStop(boolean reset) { }
        default void onFinished() { }
    }

    public interface TextDrawer {
        default void setTextPaint(Paint paint, long leftTime, int mode) { }

        default String getText(long leftTime, int mode, String originText) {
            return mode == MODE_TIME_VARIANT ?
                    String.valueOf(leftTime == 0 ? leftTime : leftTime / 1000 + 1) : originText;
        }
    }
}