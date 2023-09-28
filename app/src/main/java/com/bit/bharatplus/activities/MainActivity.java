package com.bit.bharatplus.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bit.bharatplus.R;
import com.bit.bharatplus.databinding.ActivityMainBinding;
import com.bit.bharatplus.fragments.HomeFragment;
import com.bit.bharatplus.fragments.ProfileFragment;
import com.bit.bharatplus.fragments.ShopFragment;
import com.bit.bharatplus.utils.AndroidUtils;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    SharedPreferences sp;

    @Override
    protected void onStart() {
        super.onStart();
        sp = getSharedPreferences("data", 0);
        if(!sp.getBoolean("profileCompleted", false)){
            startActivity(new Intent(MainActivity.this, CompleteProfileActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sp = getSharedPreferences("data", 0);
        String phoneNumber = sp.getString("phone", "9876543210");

//        binding.tvCurrentUser.setText(phoneNumber);
//        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent loginActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(loginActivityIntent);
//                finishAffinity();
//            }
//        });

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
                    AndroidUtils.showAlertDialog(MainActivity.this, "Warning", "Unknown Error Occurred");
                    return false;
                }

            }
        });

    }
}