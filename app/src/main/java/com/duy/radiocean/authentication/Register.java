package com.duy.radiocean.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.duy.radiocean.MainActivity;
import com.duy.radiocean.Profile;
import com.duy.radiocean.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    TextInputEditText emailEdt, passwordEdt, genderEdt, nameEdt;
    AppCompatButton btnReg;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
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
        Profile profile = new Profile(emailEdt.getText().toString(),
                nameEdt.getText().toString(),
                genderEdt.getText().toString());
        String email = nameEdt.getText().toString().replace('.',' ');
        System.out.println(profile.getEmail()+profile.getGender());
        mDatabase.child("Profiles").child(email).setValue(profile);

    }
    public void sendData(){
        writeNewUser();
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
                                        sendData();
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