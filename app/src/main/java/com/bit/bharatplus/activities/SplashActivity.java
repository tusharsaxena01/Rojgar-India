package com.bit.bharatplus.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.bit.bharatplus.LocationService;
import com.bit.bharatplus.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sp;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_splash);
        sp = getSharedPreferences("data", 0);

        new Handler().postDelayed(() -> {
            Intent intent;
            if(mAuth.getCurrentUser() == null){
            intent = new Intent(getApplicationContext(), LoginActivity.class);
            }else{
                if(!sp.getBoolean("profileCompleted", false)){
                    intent = new Intent(getApplicationContext(), CompleteProfileActivity.class);
                }else
                    intent = new Intent(getApplicationContext(), NavigationActivity.class);
            }

            // Start the LocationService
            Intent locationIntent = new Intent(getApplicationContext(), LocationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(locationIntent);
            }else{
                startService(locationIntent);
            }
            startActivity(intent);
            finishAffinity();
        },3000);
    }
}