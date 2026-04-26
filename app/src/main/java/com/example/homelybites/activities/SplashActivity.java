package com.example.homelybites.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homelybites.R;
import com.example.homelybites.utils.FirebaseHelper;
import com.example.homelybites.utils.DummyData;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // UPLOAD DUMMY DATA ON START
        // You can comment this after running the app once
        DummyData.generateDummyData();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseHelper helper = FirebaseHelper.getInstance();
            if (helper.isLoggedIn()) {
                String uid = helper.getCurrentUser().getUid();
                helper.getUser(uid, task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        String role = task.getResult().getString("role");
                        if ("cook".equals(role)) {
                            startActivity(new Intent(SplashActivity.this, CookDashboardActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, CustomerHomeActivity.class));
                        }
                    } else {
                        startActivity(new Intent(SplashActivity.this, CustomerLoginActivity.class));
                    }
                    finish();
                });
            } else {
                startActivity(new Intent(SplashActivity.this, CustomerLoginActivity.class));
                finish();
            }
        }, SPLASH_DELAY);
    }
}
