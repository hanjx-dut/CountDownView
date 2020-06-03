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
    private static final int DEFAULT_LENGTH = SizeUtils.dp2px(30);
    private static final int DEFAULT_START_ANGLE = -90;
    private static final int DEFAULT_DURATION = 2000;
    private static final int DEFAULT_INTERVAL = 16;
    private static final int DEFAULT_FINISHED_COLOR = 0xFF3C7CFC;
    private static final int DEFAULT_UNFINISHED_COLOR = 0x00000000;
    private static final int DEFAULT_STROKE = SizeUtils.dp2px(3);

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

    // 自动开始
    private boolean autoStart;
    // 总时间
    private int duration;
    // 刷新间隔 fast:11ms, normal:16ms, slow:20ms
    private int interval;
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
        if (autoStart) {
            post(this::start);
        }
    }

    private void initPaint() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(strokeWidth);
    }

    public void start() {
        start(duration);
    }

    public void start(int duration) {
        this.duration = duration;
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(duration, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                finishedAngle = Math.round(360 - 360f * millisUntilFinished / duration);
                invalidate();
                if (listener != null) {
                    listener.onTick(millisUntilFinished, finishedAngle);
                }
            }

            @Override
            public void onFinish() {
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
        // 默认宽高 30dp
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

    public interface CountDownListener {
        default void onTick(long leftTime, float finishedAngle) { }
        default void onStop(boolean reset) { }
        default void onFinished() { }
    }
}