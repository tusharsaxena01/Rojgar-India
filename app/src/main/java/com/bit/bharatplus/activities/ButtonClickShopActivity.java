package com.bit.bharatplus.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bit.bharatplus.R;
import com.bit.bharatplus.adapters.ImageSliderAdapter;
import com.bit.bharatplus.databinding.ActivityButtonClickShopBinding;
import com.bit.bharatplus.utils.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

public class ButtonClickShopActivity extends AppCompatActivity {

    ActivityButtonClickShopBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityButtonClickShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(v -> onBackPressed());
        Intent oldIntent = getIntent();
        String productName = oldIntent.getStringExtra("Product name");
        String productPrice = oldIntent.getStringExtra("Product price");
        String productBrand = oldIntent.getStringExtra("Product brand");
        String productDesc = oldIntent.getStringExtra("Product desc");
        String productCategory = oldIntent.getStringExtra("Product category");
        ArrayList<String> productImages = new ArrayList<String>();
        productImages.addAll(oldIntent.getStringArrayListExtra("Product images"));
        String productRating = oldIntent.getStringExtra("Product rating");

        binding.tvHeader.setText(productName);
        binding.productName.setText(productName);
        binding.productBrand.setText(productBrand);
        binding.productDesc.setText(productDesc);
//        binding.productCategory.setText(productCategory);
        try{
            if (!productRating.isEmpty() && !productPrice.isEmpty()) {
                binding.productRating.setText(productRating);
                binding.productPrice.setText(productPrice);
            }
        }catch (Exception e){
            AndroidUtils.showAlertDialog(this, "Error", "Unknown Error Occurred");
            AndroidUtils.showAlertDialog(this, "Warning", "Internet Connection Error, Firebase Server not Responding");

        }
        binding.productRating.setText(productRating);
        setupImages(productImages);

        binding.btnBuyNow.setOnClickListener(v -> AndroidUtils.showToast(ButtonClickShopActivity.this, "Paywall Integration not done for security reasons"));

    }

    private void setupImages(ArrayList<String> productImages) {
        ImageSliderAdapter imageSliderAdapter;

        imageSliderAdapter = new ImageSliderAdapter(this, productImages);
        binding.images.setAdapter(imageSliderAdapter);

    }


}