package com.hanjx.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hanjx.ui.CountDownView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CountDownView countDownView = findViewById(R.id.count_down_view);
        TextView finishedText = findViewById(R.id.finished_angle);
        View startBtn = findViewById(R.id.start_btn);
        View stopBtn = findViewById(R.id.stop_btn);
        startBtn.setOnClickListener(v -> countDownView.start(2000));
        stopBtn.setOnClickListener(v -> countDownView.stop(false));
        countDownView.setCountDownListener(new CountDownView.CountDownListener() {
            @Override
            public void onTick(long leftTime, float finishedAngle) {
                finishedText.setText(String.format("time: %s, angle: %s", leftTime, finishedAngle));
            }

            @Override
            public void onFinished() {
                finishedText.setText("finished");
            }
        });
    }
}