package com.bit.bharatplus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bit.bharatplus.databinding.ActivityVerifyOtpBinding;
import com.bit.bharatplus.utils.AndroidUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {
    ActivityVerifyOtpBinding binding;
    String backEndOTP;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    SharedPreferences sp;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        sp = getSharedPreferences("data", 0);
        setContentView(binding.getRoot());

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                setInProgress(false);
                Log.d("Phone Authentication", "onVerificationCompleted:" + phoneAuthCredential);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                setInProgress(false);
                Log.w("Phone Authentication", "onVerificationFailed: " + e.getMessage().toString());
            }

            @Override
            public void onCodeSent(@NonNull String newBackEndOTP, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                setInProgress(false);
                backEndOTP = newBackEndOTP;
                Toast.makeText(VerifyOTP.this, "OTP sent Successfully", Toast.LENGTH_SHORT).show();
            }
        };

        binding.tvMobile.setText(getIntent().getStringExtra("phone"));
        backEndOTP = getIntent().getStringExtra("OTP");

        otpDigitMove();

        binding.btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInProgress(true);
                if(validate()){
                    String enteredOTP = binding.etOTP1.getText().toString() +
                            binding.etOTP2.getText().toString() +
                            binding.etOTP3.getText().toString() +
                            binding.etOTP4.getText().toString() +
                            binding.etOTP5.getText().toString() +
                            binding.etOTP6.getText().toString();
                    if(backEndOTP != null){
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                                backEndOTP, enteredOTP
                        );
                        mAuth.signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        setInProgress(false);
                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            saveUser(getIntent().getStringExtra("phone"));
                                            sp.edit()
                                                            .putString("phone", getIntent().getStringExtra("phone"))
                                                                    .apply();
                                            startActivity(intent);
                                        }else{
                                            AndroidUtils.showToast(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage());
                                        }
                                    }
                                })
                        ;
                    }else{
                        AndroidUtils.showToast(getApplicationContext(),"Unable to Fetch OTP from server");
                    }
                }
                setInProgress(false);
            }
        });

        binding.tvResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(String.format("+91%s", getIntent().getStringExtra("mobile")))
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(VerifyOTP.this)
                        .setCallbacks(mCallbacks)
                        .build();
            }
        });


    }

    private void saveUser(String phoneNumber) {
        String uuid = mAuth.getUid();
        mDatabase.getReference()
                .child("Registered Numbers")
                .child(uuid)
                .setValue("");

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Phone Authentication", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("Phone Authentication", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // Todo: show warning dialog
                            }
                        }
                    }
                });
    }

    private void setInProgress(boolean inProgress) {
        if(inProgress){
            binding.pbLoading.setVisibility(View.VISIBLE);
            binding.btnVerifyOTP.setVisibility(View.GONE);
        }else{

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
        binding.etOTP1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.etOTP2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etOTP2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.etOTP3.requestFocus();
                }else{
                    binding.etOTP1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etOTP3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.etOTP4.requestFocus();
                }else{
                    binding.etOTP2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etOTP4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.etOTP5.requestFocus();
                }else{
                    binding.etOTP3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etOTP5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.etOTP6.requestFocus();
                }else{
                    binding.etOTP4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etOTP6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.btnVerifyOTP.requestFocus();
                if(!s.toString().trim().isEmpty()){
                    binding.btnVerifyOTP.requestFocus();
                }else{
                    binding.etOTP5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}