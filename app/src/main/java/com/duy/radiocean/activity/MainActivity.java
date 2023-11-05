package com.duy.radiocean.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.duy.radiocean.R;
import com.duy.radiocean.databinding.ActivityMainBinding;
import com.duy.radiocean.fragment.HomeFragment;
import com.duy.radiocean.fragment.ProfileFragment;
import com.duy.radiocean.fragment.SearchFragment;
import com.duy.radiocean.model.Song;
import com.duy.radiocean.service.MusicService;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements MusicService.OnSongChangedListener {
    ActivityMainBinding binding;
    private MusicService musicService;
    private boolean isBound = false;
    private ServiceConnection serviceConnection;
    RelativeLayout relativeLayout;
    private Button btnPlay, btnPause;
    private TextView tvTitleSongPlaying, tvArtisSongPlaying;
    private ImageView imgSongPlaying;
    private FrameLayout frameLayoutNoSong, frameLayoutWithSong;
    private Song songPlaying = new Song();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo nowPlayingFragment và hiển thị nó trong now_playing_container
        getSupportFragmentManager().beginTransaction().commit();

        replaceFragment(new HomeFragment());

        binding.bottomNavigation.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (item.getItemId() == R.id.search) {
                replaceFragment(new SearchFragment());
            }
        });
        relativeLayout = findViewById(R.id.layoutPlaying);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
                startActivity(intent);
                finish();
            }
        });

        initWidgets();
        setButtonClickListeners();
        loadPlayingSongFromService();
    }
    private void checkMusicPlaybackStatus() {
        if (isBound && musicService != null) {
            Log.d("MainActivity My Test", "checkMusicPlaybackStatus() is running");
            boolean isPlaying = musicService.isPlaying();
            updatePlayPauseButtonsVisibility(isPlaying);
        }
    }
    private void initWidgets() {
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        tvTitleSongPlaying = findViewById(R.id.txtNameSongPlaying);
        tvArtisSongPlaying = findViewById(R.id.txtSingerPlaying);
        imgSongPlaying = findViewById(R.id.imgSongPlaying);
//        frameLayoutNoSong = findViewById(R.id.now_playing_container_replace);
        frameLayoutWithSong = findViewById(R.id.now_playing_container);
    }
    private void setButtonClickListeners() {
        btnPlay.setOnClickListener(v -> {
            musicService.continueMusic();
            updatePlayPauseButtonsVisibility(true);
        });
        btnPause.setOnClickListener(v -> {
            musicService.pauseMusic();
            updatePlayPauseButtonsVisibility(false);
        });
    }
    private void updatePlayPauseButtonsVisibility(boolean isPlaying) {
        btnPlay.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
        btnPause.setVisibility(isPlaying ? View.VISIBLE : View.GONE);
    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
    public void loadPlayingSongFromService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
                musicService = binder.getService();
                isBound = true;
                musicService.setOnSongChangedListener(MainActivity.this);

                // Check if music is currently playing and set the play/pause button accordingly
                checkMusicPlaybackStatus();

                Song currentSong = musicService.getCurrentPlayingSong();
                if (currentSong != null) {
                    songPlaying = currentSong;
                    setValueForWidgets();
                    Log.d("MainActivity here", "CurrentSong is " + currentSong.getTitle());
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };
    }
    private void setValueForWidgets() {
        if (songPlaying != null) {
            Picasso.get().load(songPlaying.getImgSong()).into(imgSongPlaying);
            tvTitleSongPlaying.setText(songPlaying.getTitle());
            tvArtisSongPlaying.setText(songPlaying.getArtist());
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onSongChanged(Song newSong) {
        if (newSong != null) {
            songPlaying = newSong;
            setValueForWidgets();
            updatePlayPauseButtonsVisibility(true);
            Log.d("MainActivity here", "CurrentSong is " + newSong.getTitle());
        }
    }
}
