package com.example.signinsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Button btnCancel,btnResetPassword;
    private EditText emailForgetPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private static final String EMAIL_REGEX="^[a-zA-Z0-9+_.-]+@(.+)$";
    private String emailS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        btnResetPassword=findViewById(R.id.btnResetPassword);
        emailForgetPassword=findViewById(R.id.emailForgetPassword);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        btnCancel=findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> {
            startActivity(new Intent(ForgetPasswordActivity.this,SignInActivity.class));
            Toast.makeText(this, "Going to Sign In Interface", Toast.LENGTH_SHORT).show();

        });
        btnResetPassword.setOnClickListener(v -> {
            emailS=emailForgetPassword.getText().toString().trim();
            if (!isValidEmail(emailS)) {
                emailForgetPassword.setError("Email is invalid");
            } else {
                progressDialog.setMessage("Operation in progress");
                progressDialog.show();
                firebaseAuth.sendPasswordResetEmail(emailS).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(ForgetPasswordActivity.this,SignInActivity.class));
                        progressDialog.dismiss();
                    }else {
                        Toast.makeText(this, "Operation failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }
    private boolean isValidEmail(String email){
        Pattern pattern =Pattern.compile(EMAIL_REGEX);
        Matcher matcher=pattern.matcher(email);
        return matcher.matches();
    }
}