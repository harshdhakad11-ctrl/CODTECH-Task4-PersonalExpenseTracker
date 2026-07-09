package com.dhakad.personalexpensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.dhakad.personalexpensetracker.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imgLogo = findViewById(R.id.imgLogo);

        // Logo Animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        imgLogo.startAnimation(animation);

        // Open Main Screen after 2.5 seconds
        new Handler().postDelayed(() -> {

            startActivity(new Intent(
                    SplashActivity.this,
                    MainActivity.class
            ));

            finish();

        }, 2500);

    }
}