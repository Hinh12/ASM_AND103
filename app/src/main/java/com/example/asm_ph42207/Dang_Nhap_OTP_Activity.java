package com.example.asm_ph42207;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Dang_Nhap_OTP_Activity extends AppCompatActivity {
    TextInputEditText edPhone;
    Button btnLoginOTP, btnGetOTP;
    TextView edOTP;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky_otpactivity);

        mAuth = FirebaseAuth.getInstance();


        edPhone = findViewById(R.id.edPhone);
        edOTP = findViewById(R.id.edOTP);
        btnGetOTP = findViewById(R.id.btngetOTP);
        btnLoginOTP = findViewById(R.id.btnLoginOTP);
        LottieAnimationView back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String smsCode = phoneAuthCredential.getSmsCode();
                if (smsCode != null) {
                    edOTP.setText(smsCode);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(Dang_Nhap_OTP_Activity.this, "Xác thực thất bại", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
            }

        };

        btnGetOTP.setOnClickListener(view -> {
            String phoneNumber = edPhone.getText().toString();
            getOTP(phoneNumber);
        });

        // Xử lý khi người dùng nhấn nút đăng nhập OTP
        btnLoginOTP.setOnClickListener(view -> {
            // Xử lý đăng nhập bằng OTP
            String code = edOTP.getText().toString();
            verifyOTP(code);
        });

    }

    private void getOTP(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber("+84" + phoneNumber).setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(mCallbacks).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOTP(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Dang_Nhap_OTP_Activity.this, "Dăng nhập thành công", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = task.getResult().getUser();
                    startActivity(new Intent(Dang_Nhap_OTP_Activity.this, MainActivity.class));
                } else {
                    Log.w(TAG, "signInWithCredential: failure ", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                        Toast.makeText(Dang_Nhap_OTP_Activity.this, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

}