package com.example.signinsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private EditText fullName,email,id,phone;
    private Button btnEdit,btnLogOut;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fullName=findViewById(R.id.fullNameProfile);
        id=findViewById(R.id.idProfile);
        phone=findViewById(R.id.phoneProfile);
        email=findViewById(R.id.emailProfile);
        btnEdit=findViewById(R.id.editProfile);
        btnLogOut=findViewById(R.id.btnLogOut);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        user=firebaseAuth.getCurrentUser();
        reference=firebaseDatabase.getReference().child("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullNameS=snapshot.child("fullName").getValue().toString();
                String emailS=snapshot.child("email").getValue().toString();
                String idProfileS=snapshot.child("id").getValue().toString();
                String phoneS=snapshot.child("phone").getValue().toString();
                fullName.setText(fullNameS);
                email.setText(emailS);
                id.setText(idProfileS);
                phone.setText(phoneS);

}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error !", Toast.LENGTH_SHORT).show();

            }
        });
        btnLogOut.setOnClickListener(v -> {
            SharedPreferences preferences1=getSharedPreferences("checkBox",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences1.edit();
            editor.putBoolean("remember",false);
            editor.apply();
            firebaseAuth.signOut();
            startActivity(new Intent(ProfileActivity.this,SignInActivity.class));
            Toast.makeText(this, "Log Out succefully !", Toast.LENGTH_SHORT).show();
            finish();

        });

        btnEdit.setOnClickListener( v -> {
            String editFullName=fullName.getText().toString().trim();
            String editId=id.getText().toString().trim();
            String editPhone=phone.getText().toString().trim();
            reference.child("fullName").setValue(editFullName);
            reference.child("id").setValue(editId);
            reference.child("phone").setValue(editPhone);
            Toast.makeText(this, "Data changed succefully !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this,ProfileActivity.class));


        });
    }
}