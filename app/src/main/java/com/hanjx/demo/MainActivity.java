package com.hanjx.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hanjx.ui.CountDownView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CountDownView countDownView = findViewById(R.id.count_down_view);
        countDownView.setOnClickListener(v -> countDownView.start());
    }
}