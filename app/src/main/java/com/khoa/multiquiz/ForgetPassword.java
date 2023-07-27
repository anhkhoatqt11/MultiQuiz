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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgetPassword extends AppCompatActivity {

    EditText EmailForgotPassword;
    TextView LoginSwitchTextView;
    Button ForgotPassword;
    private FirebaseAuth mAuth;
    ProgressBar ProgressBarForgotPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        LoginSwitchTextView = findViewById(R.id.loginSwitchTextViewForgotPassword);
        ForgotPassword = findViewById(R.id.forgotButton);
        EmailForgotPassword = findViewById(R.id.EmailTextViewForgotPassword);
        ProgressBarForgotPassword = findViewById(R.id.progressBarForgotPassword);

        mAuth = FirebaseAuth.getInstance();

        LoginSwitchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });


        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressBarForgotPassword.setVisibility(View.VISIBLE);
                String email;
                email = String.valueOf(EmailForgotPassword.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(ForgetPassword.this, "Nhập email", Toast.LENGTH_SHORT).show();
                }

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ProgressBarForgotPassword.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetPassword.this, "Gửi email khôi phục thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(ForgetPassword.this, "Gửi email khôi phục thất bại.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });





    }
}
