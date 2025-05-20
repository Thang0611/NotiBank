package com.NT.banknotireader;

import android.app.Activity;
import android.os.Bundle;


import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SplashActivity extends Activity {

    private static final int SPLASH_TIME = 2000; // thời gian chờ (ms)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tạo layout đơn giản với icon ở giữa
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_icon); // icon app

        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setGravity(Gravity.CENTER);
        layout.addView(imageView);

        setContentView(layout);

        // Chuyển sang MainActivity sau một thời gian
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, SPLASH_TIME);
    }
}