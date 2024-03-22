package com.example.asm_ph42207;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dang_Nhap_Activity extends AppCompatActivity {
    TextInputEditText edEmail, edPassword;
    TextInputLayout in_email, in_pass;
    Button btnLogin;
    TextView btnDangky, btnquenMK;
    CheckBox chkRememberPass;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        mAuth = FirebaseAuth.getInstance();

        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        in_email = findViewById(R.id.in_Email);
        in_pass = findViewById(R.id.in_Pass);
        btnLogin = findViewById(R.id.btnLogin);
        btnDangky = findViewById(R.id.btnDangky01);
        btnquenMK = findViewById(R.id.btnquenMK);
        chkRememberPass = findViewById(R.id.chkRememberPass);

        btnquenMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAddress = edEmail.getText().toString();
                mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Dang_Nhap_Activity.this, "Vui lòng kiểm tra hộp thư để cập nhật lại mật khẩu", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(Dang_Nhap_Activity.this, "Lỗi gửi email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnDangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dang_Nhap_Activity.this,Dang_Ky_Activity.class);
                startActivity(intent);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Dang_Nhap_Activity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(Dang_Nhap_Activity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Dang_Nhap_Activity.this, MainActivity.class));
                                } else {
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(Dang_Nhap_Activity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}