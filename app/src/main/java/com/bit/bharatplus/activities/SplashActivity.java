package com.bit.bharatplus.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bit.bharatplus.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(mAuth.getCurrentUser() == null){
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                }else{
                intent = new Intent(getApplicationContext(), MainActivity.class);
                }
                startActivity(intent);
                finishAffinity();
            }
        },3000);
    }
}