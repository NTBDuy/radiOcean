package com.duy.radiocean.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.duy.radiocean.R;
import com.duy.radiocean.activity.MainActivity;
import com.duy.radiocean.model.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Register extends AppCompatActivity {
    TextInputEditText emailEdt, passwordEdt, genderEdt, nameEdt;
    AppCompatButton btnReg;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ArrayList<Profile> listProfile = new ArrayList<>();
    int idIdentity=0;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void writeNewUser(){
        String primaryEmailKey = emailEdt.getText().toString();
        primaryEmailKey = primaryEmailKey.replaceAll("[^a-zA-Z0-9]", "");

        Profile profile = new Profile(emailEdt.getText().toString(),
                nameEdt.getText().toString(),
                genderEdt.getText().toString());
        mDatabase.child("Profiles").child(primaryEmailKey).setValue(profile);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEdt = findViewById(R.id.email);
        passwordEdt = findViewById(R.id.password);
        genderEdt = findViewById(R.id.gender);
        nameEdt = findViewById(R.id.name);
        btnReg = findViewById(R.id.btnRegister);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        TextView backToLogIn = findViewById(R.id.login_in_regPage);
        backToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText, passwordText;
                emailText = String.valueOf(emailEdt.getText());
                passwordText = String.valueOf(passwordEdt.getText());

                if (TextUtils.isEmpty(emailText)) {
                    Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(passwordText)){
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                }
                mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    try{
                                        writeNewUser();
                                    }catch(Exception e){
                                        System.out.println("ERROR: "+ e.getMessage());
                                    }
                                    Toast.makeText(Register.this, "Account created ! ", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
    }
}