package com.bit.bharatplus.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.bit.bharatplus.activities.LoginActivity;
import com.bit.bharatplus.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sp = getSharedPreferences("data", 0);
        String phoneNumber = sp.getString("phone", "9876543210");

        binding.tvCurrentUser.setText(phoneNumber);
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent loginActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginActivityIntent);
                finishAffinity();
            }
        });

    }
}