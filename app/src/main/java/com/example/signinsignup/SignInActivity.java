package com.example.signinsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    private TextView goToSignUp,goToForgetPassword;
    private EditText email,password ;
    private Button btnSignIn;
    private CheckBox rememberMe;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private static final String EMAIL_REGEX="^[a-zA-Z0-9+_.-]+@(.+)$";
    private String emailS,passwordS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        goToForgetPassword=findViewById(R.id.goToForgetPassword);
        goToSignUp=findViewById(R.id.goToSignUp);
        email=findViewById(R.id.emailSignIn);
        password=findViewById(R.id.passwordSignIn);
        btnSignIn=findViewById(R.id.btnSignIn);
        rememberMe=findViewById(R.id.rememberMe);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                Toast.makeText(SignInActivity.this, "Going To Sign Up form  ", Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences preferences=getSharedPreferences("checkBox",MODE_PRIVATE);

        boolean resCheckBox=preferences.getBoolean("remember",false);
        if (resCheckBox){
            startActivity(new Intent(SignInActivity.this,ProfileActivity.class));

        }else {
            Toast.makeText(this, "Please sign In !", Toast.LENGTH_SHORT).show();
        }
        rememberMe.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()){
                SharedPreferences preferences1=getSharedPreferences("checkBox",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences1.edit();
                editor.putBoolean("remember",true);
                editor.apply();
            }else if (!buttonView.isChecked()){
                SharedPreferences preferences1=getSharedPreferences("checkBox",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences1.edit();
                editor.putBoolean("remember",false);
                editor.apply();
            }
        });
        goToForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, ForgetPasswordActivity.class));
                Toast.makeText(SignInActivity.this, "Going To Forget Password form  ", Toast.LENGTH_SHORT).show();

            }
        });

         btnSignIn.setOnClickListener(v -> {
            emailS = email.getText().toString().trim();
            passwordS = password.getText().toString().trim();
            if (!isValidEmail(emailS)) {
                email.setError("Email non valide");
            } else if (passwordS.isEmpty() || passwordS.length() < 6) {
                password.setError("Password is invalid ");
            } else {
                login(emailS,passwordS);
            }
        });
    }

    private void login(String emailS, String passwordS) {
        progressDialog.setMessage("Operation in progress");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(emailS,passwordS).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                checkEmailVerification();
            } else {
                Toast.makeText(this, "Sign In failed !", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser loggedUser = firebaseAuth.getCurrentUser();
        if (loggedUser != null){
            if (loggedUser.isEmailVerified()){
                finish();
                startActivity(new Intent(SignInActivity.this, ProfileActivity.class));
                progressDialog.dismiss();
            } else {
                Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                progressDialog.dismiss();
            }
        }
    }


   private boolean isValidEmail(String email) {
        Pattern pattern =Pattern.compile(EMAIL_REGEX);
        Matcher matcher=pattern.matcher(email);
        return matcher.matches();
    }
}