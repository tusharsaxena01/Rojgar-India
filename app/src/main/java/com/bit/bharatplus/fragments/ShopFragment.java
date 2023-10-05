package com.bit.bharatplus.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bit.bharatplus.activities.LoginActivity;
import com.bit.bharatplus.databinding.FragmentShopBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ShopFragment extends Fragment {
    FragmentShopBinding binding;
    FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        binding.tvCurrentUser.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getPhoneNumber());
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });



        return binding.getRoot();
    }
}
