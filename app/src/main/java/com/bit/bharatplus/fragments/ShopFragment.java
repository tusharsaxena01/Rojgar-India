package com.bit.bharatplus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bit.bharatplus.databinding.FragmentShopBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ShopFragment extends Fragment {
    FragmentShopBinding binding;
    FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();


        return binding.getRoot();
    }

}
