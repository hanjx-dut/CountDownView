package com.hanjx.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;

public class CountDownView extends View {
    private static final int LENGTH_DEFAULT = SizeUtils.dp2px(30);
    private static final int START_ANGLE_DEFAULT = -90;
    private static final int DURATION_DEFAULT = 2000;
    private static final int INTERVAL_DEFAULT = 16;
    private static final int INTERVAL_HIGH_ACCURACY = 11;
    private static final int INTERVAL_LOW_ACCURACY = 20;
    private static final int COLOR_FINISHED_DEFAULT = 0xFF00FF00;
    private static final int COLOR_UNFINISHED_DEFAULT = 0xFFFF0000;

    private int width;
    private int height;
    private int length;

    private int colorFinished;
    private int colorUnfinished;
    private int strokeWidth = SizeUtils.dp2px(3);

    private Paint circlePaint;
    private RectF oval = new RectF();

    private int finishedAngle;
    private CountDownTimer timer;
    private int interval;

    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        colorFinished = COLOR_FINISHED_DEFAULT;
        colorUnfinished = COLOR_UNFINISHED_DEFAULT;
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

        int left = (width - length) / 2 + strokeWidth / 2;
        int right = left + length - strokeWidth;
        int top = (height - length) / 2 + strokeWidth / 2;
        int bottom = top + length - strokeWidth;
        oval.set(left, top, right, bottom);

        circlePaint.setColor(colorFinished);
        canvas.drawArc(oval, START_ANGLE_DEFAULT, finishedAngle, false, circlePaint);

        circlePaint.setColor(colorUnfinished);
        canvas.drawArc(oval, START_ANGLE_DEFAULT + finishedAngle, 360 - finishedAngle, false, circlePaint);
    }

    public void start() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(DURATION_DEFAULT, INTERVAL_DEFAULT) {
            @Override
            public void onTick(long millisUntilFinished) {
                finishedAngle = Math.round(360 - 360f * millisUntilFinished / DURATION_DEFAULT);
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