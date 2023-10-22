package com.bit.bharatplus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bit.bharatplus.databinding.ActivityVerifyOtpBinding;
import com.bit.bharatplus.utils.AndroidUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class VerifyOTP extends AppCompatActivity {
    ActivityVerifyOtpBinding binding;
    String backEndOTP;
    String phoneNumber;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    SharedPreferences sp;
    long timeoutSeconds = 60L;

    ArrayList<EditText> otps = new ArrayList<EditText>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        sp = getSharedPreferences("data", 0);
        setContentView(binding.getRoot());

        binding.tvResendOTP.setEnabled(false); // Disable the button initially


        addEditTextToArray(binding);
        phoneNumber = getIntent().getStringExtra("phone");

        binding.tvMobile.setText(phoneNumber);
        backEndOTP = getIntent().getStringExtra("OTP");

        otpDigitMove();

        binding.btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInProgress(true);
                if (validate()) {
                    String enteredOTP = binding.etOTP1.getText().toString() +
                            binding.etOTP2.getText().toString() +
                            binding.etOTP3.getText().toString() +
                            binding.etOTP4.getText().toString() +
                            binding.etOTP5.getText().toString() +
                            binding.etOTP6.getText().toString();
                    if (backEndOTP != null) {
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                                backEndOTP, enteredOTP
                        );
                        signIn(phoneAuthCredential);

                    } else {
                        AndroidUtils.showToast(getApplicationContext(), "Unable to Fetch OTP from server");
                    }
                }
                setInProgress(false);
            }
        });

        // enable resend btn after 30 seconds
        startResendTimer();
        binding.tvResendOTP.setOnClickListener(v -> {
            AndroidUtils.showToast(getApplicationContext(), "Clicked");
            // Disable the button to prevent multiple clicks
            binding.tvResendOTP.setEnabled(false);

            // Add your code to resend OTP here
            new LoginActivity().sendOTP(phoneNumber, true);
        });

        // code ends

    }

    private void startResendTimer() {
        binding.tvResendOTP.setEnabled(true);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                String message = "Resend OTP in "+timeoutSeconds+" secs";
                runOnUiThread(() -> binding.tvResendOTPMessage.setText(message));
                if(timeoutSeconds<=0){
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(() -> binding.tvResendOTP.setEnabled(true));
                }
            }
        }, 0, 1000);
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true);

        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), CompleteProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        sp.edit()
                                .putString("phone", phoneNumber)
                                .putBoolean("profileCompleted", false)
                                .apply();
                        startActivity(intent);
                    } else {
                        AndroidUtils.showToast(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage());
                        AndroidUtils.showAlertDialog(VerifyOTP.this, "Error", Objects.requireNonNull(task.getException()).getMessage());
                    }
                })
        ;
    }

    private void addEditTextToArray(ActivityVerifyOtpBinding binding) {
        otps.add(binding.etOTP1);
        otps.add(binding.etOTP2);
        otps.add(binding.etOTP3);
        otps.add(binding.etOTP4);
        otps.add(binding.etOTP5);
        otps.add(binding.etOTP6);
    }


    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            binding.pbLoading.setVisibility(View.VISIBLE);
            binding.btnVerifyOTP.setVisibility(View.GONE);
        } else {
            binding.pbLoading.setVisibility(View.GONE);
            binding.btnVerifyOTP.setVisibility(View.VISIBLE);
        }

    }

    private boolean validate() {
        String opt1, opt2, opt3, opt4, opt5, opt6;
        opt1 = binding.etOTP1.getText().toString();
        opt2 = binding.etOTP2.getText().toString();
        opt3 = binding.etOTP3.getText().toString();
        opt4 = binding.etOTP4.getText().toString();
        opt5 = binding.etOTP5.getText().toString();
        opt6 = binding.etOTP6.getText().toString();
        return !opt1.isEmpty() && !opt2.isEmpty() && !opt3.isEmpty() && !opt4.isEmpty() && !opt5.isEmpty() && !opt6.isEmpty();
    }

    private void otpDigitMove() {
        for (int i = 0; i < otps.size(); i++) {
            int finalI = i;
            otps.get(i).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().trim().isEmpty()) {
                        if (finalI != otps.size() - 1)
                            otps.get(finalI + 1).requestFocus();
                        else {
                            binding.btnVerifyOTP.requestFocus();
                            closeKeyboard();
                        }
                    } else {
                        if (finalI != 0)
                            otps.get(finalI - 1).requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    private void closeKeyboard() {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }
}