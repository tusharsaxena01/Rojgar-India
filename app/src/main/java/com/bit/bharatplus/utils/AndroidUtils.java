package com.bit.bharatplus.utils;

import static android.R.color.transparent;

import static com.bit.bharatplus.R.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bit.bharatplus.R;
import com.bit.bharatplus.databinding.DialogPopupBinding;

public class AndroidUtils {
    AlertDialog dialog;
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    void showDialog(Context context, String dialogType, String message){
        DialogPopupBinding binding = DialogPopupBinding.inflate(LayoutInflater.from(context));
        int[] backgrounds = {
                drawable.rounded_background_success,
                drawable.rounded_background_error
        };



    }
}
