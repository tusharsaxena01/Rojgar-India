package com.bit.bharatplus.utils;

import static android.R.color.transparent;

import static com.bit.bharatplus.R.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bit.bharatplus.databinding.DialogConfirmBinding;
import com.bit.bharatplus.databinding.DialogPopupBinding;

public class AndroidUtils {
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    public static void showAlertDialog(Context context, String dialogType, String message) {

        int[] backgrounds = {
                drawable.rounded_background_success,
                drawable.rounded_background_error,
                drawable.baseline_warning_amber_24
        };

        int[] icons = {
                drawable.baseline_check_circle_outline_24,
                drawable.baseline_error_outline_24,
                drawable.baseline_warning_amber_24
        };

        context.setTheme(style.Theme_BharatPlus);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        DialogPopupBinding binding = DialogPopupBinding.inflate(LayoutInflater.from(context));
        builder.setView(binding.getRoot());

        // Set the header and message in the dialog
        binding.tvDialogHeader.setText(dialogType);
        binding.tvDialogDesc.setText(message);

        switch (dialogType){
            case "Success":
//                binding.ivDialogIcon.setImageResource(backgrounds[0]);
                binding.btnOK.setBackgroundResource(backgrounds[0]);
                binding.ivDialogIcon.setImageResource(icons[0]);
                break;
            case "Error":
                binding.btnOK.setBackgroundResource(backgrounds[1]);
                binding.ivDialogIcon.setImageResource(icons[1]);
                break;
            case "Warning":
                binding.btnOK.setBackgroundResource(backgrounds[2]);
                binding.ivDialogIcon.setImageResource(icons[2]);
                break;
        }

        AlertDialog dialog = builder.create();

        // close the dialog when ok is pressed
        binding.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

//        binding.clMain.setBackgroundResource(transparent);
        dialog.getWindow().setBackgroundDrawableResource(transparent);

        dialog.show();
        




    }
}
