package com.bit.bharatplus.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bit.bharatplus.LocationService;
import com.bit.bharatplus.R;
import com.bit.bharatplus.databinding.ActivityMainBinding;
import com.bit.bharatplus.fragments.HomeFragment;
import com.bit.bharatplus.fragments.ProfileFragment;
import com.bit.bharatplus.fragments.ShopFragment;
import com.bit.bharatplus.utils.AndroidUtils;
import com.google.android.material.navigation.NavigationBarView;

public class NavigationActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    SharedPreferences sp;

    @Override
    protected void onStart() {
        super.onStart();
        sp = getSharedPreferences("data", 0);
        if(!sp.getBoolean("profileCompleted", false)){
            startActivity(new Intent(NavigationActivity.this, CompleteProfileActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sp = getSharedPreferences("data", 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, LocationService.class));
        }else{
            startService(new Intent(this, LocationService.class));
        }


        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Todo: update fragment accordingly
                Fragment currentFragment = null;
                if(item.getItemId() == R.id.bn_home){
                    currentFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.bn_shop) {
                    currentFragment = new ShopFragment();
                } else if (item.getItemId() == R.id.bn_profile) {
                    currentFragment = new ProfileFragment();
                }
                if(currentFragment != null) {

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragContainer, currentFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                }else{
                    AndroidUtils.showAlertDialog(NavigationActivity.this, "Warning", "Unknown Error Occurred");
                    return false;
                }

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, LocationService.class));
        }else{
            startService(new Intent(this, LocationService.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop the LocationService
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
    }


}