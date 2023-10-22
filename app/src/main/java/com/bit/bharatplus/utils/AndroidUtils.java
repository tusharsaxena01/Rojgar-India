package com.bit.bharatplus.utils;

import static android.R.color.transparent;
import static com.bit.bharatplus.R.drawable;
import static com.bit.bharatplus.R.style;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bit.bharatplus.activities.CompleteProfileActivity;
import com.bit.bharatplus.databinding.DialogAlertBinding;

public class AndroidUtils {
    static AlertDialog currentDialog;
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    public static void showAlertDialog(Context context, String dialogType, String message) {

        int[] backgrounds = {
                drawable.rounded_background_success,
                drawable.rounded_background_error,
                drawable.rounded_background_warning
        };

        int[] icons = {
                drawable.baseline_check_circle_outline_24,
                drawable.baseline_error_outline_24,
                drawable.baseline_crisis_alert_24
        };

        int[] outlines = {
                drawable.outline_success,
                drawable.outline_error,
                drawable.outline_warning
        };

        context.setTheme(style.Theme_BharatPlus);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        DialogAlertBinding binding = DialogAlertBinding.inflate(LayoutInflater.from(context));
        builder.setView(binding.getRoot());

        // Set the header and message in the dialog
        binding.tvDialogHeader.setText(dialogType);
        binding.tvDialogDesc.setText(message);

        int index=1;
        switch (dialogType){
            case "Success":
                index = 0;
                binding.btnOK.setVisibility(View.GONE);
                break;
            case "Error":
                break;
            case "Warning":
                index = 2;
                break;
        }

        binding.llMain.setBackgroundResource(outlines[index]);
        binding.ivDialogIcon.setBackgroundResource(backgrounds[index]);
        binding.llDialogIcon.setBackgroundResource(backgrounds[index]);
        binding.btnOK.setBackgroundResource(backgrounds[index]);
        binding.ivDialogIcon.setImageResource(icons[index]);

        AlertDialog dialog = builder.create();

        // close the dialog when ok is pressed
        binding.btnOK.setOnClickListener(v -> dialog.dismiss());


//        binding.clMain.setBackgroundResource(transparent);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.getWindow().setBackgroundDrawableResource(transparent);

        dialog.show();

        if(CompleteProfileActivity.isValidContextForGlide(context))
            currentDialog = dialog;
    }

    public static void dismissCurrentDialog(){
        if(currentDialog.isShowing())
            currentDialog.dismiss();
    }


}
