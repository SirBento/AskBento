package com.example.askbento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    TextView appName;
    LottieAnimationView lottieAnimationView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        appName = findViewById(R.id.appname);
        lottieAnimationView = findViewById(R.id.lottie);
        mAuth = FirebaseAuth.getInstance();

        YoYo.with(Techniques.Pulse).duration(1000).repeat(10).playOn(appName);

        FirebaseUser LoggediNuser = mAuth.getCurrentUser();

        if (LoggediNuser != null) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            }, 5000);

        }else
            new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    startActivity(new Intent(SplashScreen.this,Login.class));
                    finish();
            }
        }, 5000);



    }



}