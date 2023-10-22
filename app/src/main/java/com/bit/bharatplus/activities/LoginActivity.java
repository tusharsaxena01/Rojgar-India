package com.bit.bharatplus.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bit.bharatplus.R;
import com.bit.bharatplus.databinding.ActivityLoginBinding;
import com.bit.bharatplus.utils.AndroidUtils;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding activityLoginBinding;
    FirebaseAuth mAuth;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    long timeoutSeconds = 60L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_BharatPlus); // Set the app theme before super.onCreate()
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        setContentView(activityLoginBinding.getRoot());

        setInProgress(false);

        // move the focus to edittext when clicked on ll
        activityLoginBinding.llMobileNo.setOnClickListener(v -> activityLoginBinding.etMobile.requestFocus());

        // change focus to button
        changeFocus();

        // when clicked on get otp btn
        activityLoginBinding.btnGetOTP.setOnClickListener(v -> {
            setInProgress(true);
            if(validatePhone(activityLoginBinding.etMobile.getText().toString())){

                String phoneNumber = "+91"+activityLoginBinding.etMobile.getText().toString();

                sendOTP(phoneNumber, false);

            }else{
                // if return false then get focus back on phone number edittext
                setInProgress(false);
                activityLoginBinding.etMobile.setError("Invalid Mobile Number");
                activityLoginBinding.llMobileNo.requestFocus();
            }
        });
    }

    private void changeFocus() {
        activityLoginBinding.etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 10){
                    closeKeyboard();
                    activityLoginBinding.btnGetOTP.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 10){
                    closeKeyboard();
                    activityLoginBinding.btnGetOTP.requestFocus();
                }
            }
        });
    }


    private void closeKeyboard()
    {
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
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void sendOTP(String phoneNumber, boolean isResend) {

        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneNumber);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                setInProgress(false);
                                AndroidUtils.showToast(getApplicationContext(), "Verification Failed"+" "+e.getMessage());
                                AndroidUtils.showAlertDialog(LoginActivity.this, "Error", e.getMessage());
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
//                                AndroidUtils.showToast(getApplicationContext(), "OTP Send Successfully");
                                Intent verifyOTPIntent = new Intent(getApplicationContext(), VerifyOTP.class);
                                verifyOTPIntent.putExtra("phone", phoneNumber);
                                verifyOTPIntent.putExtra("OTP", verificationCode);
                                startActivity(verifyOTPIntent);
                            }
                        });

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(
                    builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(
                    builder.build()
            );
        }

    }

    private void signIn(String phoneNumber) {
        //login and go to next activity
        Intent verifyOTPIntent = new Intent(getApplicationContext(), VerifyOTP.class);
        verifyOTPIntent.putExtra("phone", phoneNumber);
        verifyOTPIntent.putExtra("OTP", verificationCode);
        startActivity(verifyOTPIntent);
    }

    private void setInProgress(boolean inProgress) {
        if(inProgress){
            activityLoginBinding.btnGetOTP.setText("");
            activityLoginBinding.btnGetOTP.setEnabled(false);
            activityLoginBinding.pbLoading.setVisibility(View.VISIBLE);
        }else{
            activityLoginBinding.pbLoading.setVisibility(View.GONE);
            activityLoginBinding.btnGetOTP.setText("Get OTP");
            activityLoginBinding.btnGetOTP.setEnabled(true);
        }
    }


    private boolean validatePhone(String text) {
        // check if phone number is of 10 length
        return text.length() == 10;
    }
}