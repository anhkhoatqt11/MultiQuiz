package com.khoa.multiquiz;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    EditText emailTextView, passwordTextView;
    TextView loginSwitchTextView;
    Button registerButton;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailTextView = findViewById(R.id.emailTextViewRegister);
        passwordTextView = findViewById(R.id.passwordTextViewRegister);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBarRegister);
        loginSwitchTextView = findViewById(R.id.loginSwitchTextView);

        loginSwitchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email,username, password;
                email = String.valueOf(emailTextView.getText());
                password = String.valueOf(passwordTextView.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Nhập email", Toast.LENGTH_SHORT).show();
                }


                if (TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Nhập mật khẩu", Toast.LENGTH_SHORT).show();
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Đăng ký thành công.", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(Register.this, "Đăng ký thất bại.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

}
