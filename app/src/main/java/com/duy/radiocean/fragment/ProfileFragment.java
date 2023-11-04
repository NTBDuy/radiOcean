package com.duy.radiocean.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
public class ProfileFragment extends Fragment implements MusicService.OnSongChangedListener{

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
    Button btnPlay, btnPause;
    private Intent serviceIntent;
    private boolean mBound = false;
    private MusicService mService;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidgets();
        setButtonClickListeners();
    }

    private void initWidgets() {
        btnPlay = requireActivity().findViewById(R.id.btnPlay);
        btnPause = requireActivity().findViewById(R.id.btnPause);
    }

    private void setButtonClickListeners() {
        btnPlay.setOnClickListener(v -> {
            Log.d("TEST PLAYING SONG", "You are click on " + getClass());
            if (mBound) {
                mService.continueSong();
                updatePlayPauseButtonsVisibility(true);
            }
        });

        btnPause.setOnClickListener(v -> {
            Log.d("TEST PLAYING SONG", "You are click on " + getClass());
            if (mBound) {
                mService.pauseMusic();
                updatePlayPauseButtonsVisibility(false);
            }
        });
    }

    private void updatePlayPauseButtonsVisibility(boolean isPlaying) {
        btnPlay.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
        btnPause.setVisibility(isPlaying ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        bindMusicService();
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindMusicService();
    }

    private void bindMusicService() {
        serviceIntent = new Intent(getActivity(), MusicService.class);
        requireActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindMusicService() {
        if (mBound) {
            requireActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            mService.setOnSongChangedListener(ProfileFragment.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

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
    public void onSongChanged(Song newSong) {

    }
}





















