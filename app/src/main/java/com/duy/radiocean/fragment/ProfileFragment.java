package com.duy.radiocean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.duy.radiocean.R;
import com.duy.radiocean.authentication.LogIn;
import com.duy.radiocean.model.Profile;
import com.duy.radiocean.model.Song;
import com.duy.radiocean.service.MusicService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment implements MusicService.OnSongChangedListener{
    FirebaseAuth auth;
    FirebaseUser user;
    Button btnLogout;
    private TextView name, email, avatar;
    ArrayList<Profile> listProfile = new ArrayList<>();

    public ProfileFragment() {}
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
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(v.getContext(), LogIn.class);
            startActivity(intent);
            System.out.println("successful");
        });
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        avatar = view.findViewById(R.id.Profile_avatar);
        try{
            getdata();
        } catch(Exception e){
            System.out.println("throw: "+e.getMessage());
        }
        return view;
    }
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
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
                            listProfile.add(prof);
                        }catch (Exception e){
                            System.out.println("Throw: "+e.getMessage());
                        }
                    }
                    System.out.println(listProfile);
                    for (Profile profile: listProfile){
                        if(profile.getEmail().equals(user.getEmail())){
                            name.setText(profile.getName());
                            email.setText(profile.getEmail());
                            avatar.setText(String.valueOf(profile.getName().charAt(0)));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onSongChanged(Song newSong) {}

}





















