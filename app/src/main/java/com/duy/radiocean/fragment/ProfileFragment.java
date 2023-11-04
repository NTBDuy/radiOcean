package com.duy.radiocean.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.duy.radiocean.Profile;
import com.duy.radiocean.R;
import com.duy.radiocean.authentication.LogIn;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
public class ProfileFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    Button btnLogout;
    ArrayList<Profile> listProfile = new ArrayList<>();

    public ProfileFragment() {
    }
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private TextView name, email;
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container, false);
        btnLogout = view.findViewById(R.id.btnLogout);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(v.getContext(), LogIn.class);
                startActivity(intent);
                System.out.println("successful");
            }
        });


        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        try{
            getdata();
        } catch(Exception e){
            System.out.println("throw: "+e.getMessage());
        }
        return view;
    }
    public int flag = 0;
    public void getdata(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("Profiles");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listProfile.clear();
                    for (DataSnapshot profileSnapshot : snapshot.getChildren()) {
                        try{
                            Profile prof = profileSnapshot.getValue(Profile.class);
                        }catch (Exception e){
                            System.out.println("Throw: "+e.getMessage());
                        }
                    }
                    System.out.println(listProfile);
                    for (Profile profile: listProfile){
                        if(profile.getEmail().equals(user.getEmail())){
                            name.setText(profile.getName());
                            email.setText(profile.getEmail());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}





















