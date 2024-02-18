package com.example.signinsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.signinsignup.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private TextView goToSignIn;
    private EditText fullName,email,idSignUp,phone,password;
    private Button btnSignUp,register;
    private String fullNameS,emailS,idSignUpS,phoneS,passwordS;
    private static final String EMAIL_REGEX="^[a-zA-Z0-9+_.-]+@(.+)$";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fullName=findViewById(R.id.fullNameSignUp);
        email=findViewById(R.id.emailSignUp);
        idSignUp=findViewById(R.id.idSignUp);
        phone=findViewById(R.id.phoneSignUp);
        password=findViewById(R.id.passwordSignUp);
        goToSignIn=findViewById(R.id.goToLogin);
        register=findViewById(R.id.register);

        //firebase
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        register.setOnClickListener(v -> {
            if (validate()){
                progressDialog.setMessage("Please wait..!");
                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(emailS,passwordS).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        sendEmailVerification();
                    }else{

                        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        goToSignIn.setOnClickListener(v -> {
           startActivity(new Intent(MainActivity.this, SignInActivity.class));
            Toast.makeText(MainActivity.this, "Going To Sign In Form ", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendEmailVerification() {
        FirebaseUser loggeduser = firebaseAuth.getCurrentUser();
        if (loggeduser!=null){
            loggeduser.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    sendUserData();
                    progressDialog.dismiss();
                    Toast.makeText(this, "Registration done ! Verification Email was sent", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this,SignInActivity.class));
                    finish();

                } else {
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            });
        }
    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myRef=firebaseDatabase.getReference("Users");
        User user=new User(fullNameS,emailS,idSignUpS,phoneS,passwordS);
        myRef.child(""+firebaseAuth.getUid()).setValue(user);

    }

    private boolean validate() {
        boolean res=false;
        fullNameS=fullName.getText().toString().trim();
        emailS=email.getText().toString().trim();
        idSignUpS=idSignUp.getText().toString().trim();
        phoneS=phone.getText().toString().trim();
        passwordS=password.getText().toString().trim();

        if (fullNameS.isEmpty()||fullNameS.length()<7){
            fullName.setError("Full name is invalid !");
        }
        else if(emailS.isEmpty()||!isValidEmail(emailS)){
            email.setError("Email is invalid !");
        }
        else if(idSignUp.length()!=8){
            idSignUp.setError("ID is invalid !");
        }
        else if (phoneS.length()!=8){
            phone.setError("Phone is invalid !");
        }
        else if (passwordS.isEmpty()||passwordS.length()<6){
            password.setError("Password is invalid !");
        } else {
            res=true;
        }
        return res;
    }

   private boolean isValidEmail(String email){
        Pattern pattern =Pattern.compile(EMAIL_REGEX);
        Matcher matcher=pattern.matcher(email);
        return matcher.matches();
    }
}