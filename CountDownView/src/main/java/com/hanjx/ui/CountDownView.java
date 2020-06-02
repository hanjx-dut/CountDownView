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

public class CountDownView extends View {
    private static final int LENGTH_DEFAULT = SizeUtils.dp2px(30);
    private static final int START_ANGLE_DEFAULT = -90;
    private static final int DEFAULT_DURATION = 2000;
    private static final int DEFAULT_INTERVAL = 16;
    private static final int DEFAULT_FINISHED_COLOR = 0xFF0000FF;
    private static final int DEFAULT_UNFINISHED_COLOR = 0x00000000;
    private static final int DEFAULT_STROKE = SizeUtils.dp2px(3);

    private float width;
    private float height;
    private float length;

    private int finishedColor;
    private int unfinishedColor;
    private float strokeWidth;

    private Paint circlePaint;
    private RectF oval = new RectF();

    private int duration;
    private int interval;
    private int finishedAngle;
    private CountDownTimer timer;

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
        finishedColor = a.getColor(R.styleable.CountDownView_finishedColor, DEFAULT_FINISHED_COLOR);
        unfinishedColor = a.getColor(R.styleable.CountDownView_unfinishedColor, DEFAULT_UNFINISHED_COLOR);
        strokeWidth = a.getDimension(R.styleable.CountDownView_paintStroke, DEFAULT_STROKE);
        a.recycle();

        initPaint();
    }

    private void initPaint() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT ?
                MeasureSpec.makeMeasureSpec(LENGTH_DEFAULT, MeasureSpec.EXACTLY) : widthMeasureSpec;
        int heightSpec = getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT ?
                MeasureSpec.makeMeasureSpec(LENGTH_DEFAULT, MeasureSpec.EXACTLY) : heightMeasureSpec;

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

        float left = (width - length) / 2 + strokeWidth / 2;
        float right = left + length - strokeWidth;
        float top = (height - length) / 2 + strokeWidth / 2;
        float bottom = top + length - strokeWidth;
        oval.set(left, top, right, bottom);

        circlePaint.setColor(finishedColor);
        canvas.drawArc(oval, START_ANGLE_DEFAULT, finishedAngle, false, circlePaint);

        circlePaint.setColor(unfinishedColor);
        canvas.drawArc(oval, START_ANGLE_DEFAULT + finishedAngle,
                360 - finishedAngle, false, circlePaint);
    }

    public void start() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(duration, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                finishedAngle = Math.round(360 - 360f * millisUntilFinished / duration);
                invalidate();
            }

            @Override
            public void onFinish() {
                finishedAngle = 360;
                invalidate();
            }
        };
        timer.start();
    }
}