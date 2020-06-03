package com.hanjx.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.SizeUtils;
import com.hanjx.ui.CountDownView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CountDownView countDownView = findViewById(R.id.count_down_5);
        countDownView.setTextDrawer(new CountDownView.TextDrawer() {
            @Override
            public void setTextPaint(Paint paint, long leftTime, int textMode) {
                if (leftTime < 2000) {
                    paint.setTextSize(SizeUtils.sp2px(12));
                }
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setColor(0xFFFF802E);
            }

            @Override
            public String getText(long leftTime, int mode, String originText) {
                if (leftTime < 2000) {
                    return "跳过";
                }
                return String.format("%ss", leftTime == 0 ? leftTime : leftTime / 1000 + 1);
            }
        });
        countDownView.setCountDownListener(new CountDownView.CountDownListener() {
            @Override
            public void onTick(long leftTime, float finishedAngle) {

            }

            @Override
            public void onStop(boolean reset) {

            }

            @Override
            public void onFinished() {

            }
        });
        countDownView.post(countDownView::start);

        View reset = findViewById(R.id.reset);
        reset.setOnClickListener(v -> {
            CountDownView count = findViewById(R.id.count_down_1);
            count.start();
            count = findViewById(R.id.count_down_2);
            count.start();
            count = findViewById(R.id.count_down_3);
            count.start();
            count = findViewById(R.id.count_down_4);
            count.start();
            count = findViewById(R.id.count_down_5);
            count.start();
        });
    }
}