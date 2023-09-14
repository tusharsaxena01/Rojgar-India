package com.bit.bharatplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bit.bharatplus.databinding.ActivityLoginBinding;
import com.bit.bharatplus.utils.AndroidUtils;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding activityLoginBinding;
    FirebaseAuth mAuth;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    long timeoutSeconds = 60L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        setContentView(activityLoginBinding.getRoot());



        // move the focus to edittext when clicked on ll
        activityLoginBinding.llMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityLoginBinding.etMobile.requestFocus();
            }
        });

        // when clicked on get otp btn
        activityLoginBinding.btnGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }

    private void sendOTP(String phoneNumber, boolean isResend) {
        activityLoginBinding.pbLoading.setVisibility(View.VISIBLE);

        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtils.showToast(getApplicationContext(), "Verification Failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtils.showToast(getApplicationContext(), "OTP Send Successfully");
                                setInProgress(false);
                            }
                        });

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(
                    builder.setForceResendingToken(resendingToken).build()
            );
        }else{
            PhoneAuthProvider.verifyPhoneNumber(
                    builder.build()
            );
        }

        activityLoginBinding.pbLoading.setVisibility(View.GONE);
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        //login and go to next activity

    }

    private void setInProgress(boolean inProgress) {
        if(inProgress){
            activityLoginBinding.pbLoading.setVisibility(View.VISIBLE);
        }else{
            activityLoginBinding.pbLoading.setVisibility(View.GONE);
        }
    }


    private boolean validatePhone(String text) {
        // check if phone number is of 10 length
        return text.length() == 10;
    }
}