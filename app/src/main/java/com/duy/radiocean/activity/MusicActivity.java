package com.duy.radiocean.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.duy.radiocean.R;
import com.duy.radiocean.model.Song;
import com.duy.radiocean.service.MusicService;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicActivity extends AppCompatActivity implements MusicService.OnSongChangedListener {
    // ...
    private MusicService musicService;
    private boolean isBound = false;
    private ServiceConnection serviceConnection;
    private ImageButton btnBackFromMusicAct, btnPlay, btnSuff, btnLoop, btnNext, btnPrev;
    private CircleImageView imgSong, imgDisk;
    private TextView tvTitle, tvArtist, tvAlbum, tvCurTime, tvTotalTime;
    private SeekBar seekBar;
    private Song songPlaying = new Song();

    private final BroadcastReceiver seekBarReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "UPDATE_SEEK_BAR")) {
                int currentPosition = intent.getIntExtra("CURRENT_POSITION", 0);
                updateSeekBarPosition(currentPosition);
                updatePlayPauseButtonsVisibility(musicService.isPlaying());
            }
        }
    };
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_new);
        initWidgets();
        setClickFunc();
        loadPlayingSongFromService();
        // Register the broadcast receiver for SeekBar updates
        registerReceiver(seekBarReceiver, new IntentFilter("UPDATE_SEEK_BAR"));
    }
    private void initWidgets() {
        btnBackFromMusicAct = findViewById(R.id.btnBackFromMusicNew);
        btnPlay = findViewById(R.id.btnPlayFromMusicNew);
//        btnPause = findViewById(R.id.btnPauseFromMusicActi);
        btnNext = findViewById(R.id.btnNextFromMusicNew);
        btnPrev = findViewById(R.id.btnPreviousFromMusicNew);
        btnSuff = findViewById(R.id.btnShuffle);
        btnLoop = findViewById(R.id.btnLoop);
        imgSong = findViewById(R.id.imgSongPlayingFromMusicNew);
        imgDisk = findViewById(R.id.imgDiskSongPlayingFromMusicNew);
        tvTitle = findViewById(R.id.tvSongTitleNew);
        tvArtist = findViewById(R.id.tvSongArtistNew);
        tvAlbum = findViewById(R.id.tvSongAlbumNew);
        tvCurTime = findViewById(R.id.tvTimeCurrentNew);
        tvTotalTime = findViewById(R.id.tvTimeTotalNew);
        seekBar = findViewById(R.id.seekBarNew);
    }
    private void setClickFunc() {
        btnBackFromMusicAct.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });


        btnPlay.setOnClickListener(v -> {
            if (musicService.isPlaying()) {
                musicService.pauseMusic();
                updatePlayPauseButtonsVisibility(false);
            } else {
                musicService.continueMusic();
                updatePlayPauseButtonsVisibility(true);
            }
        });

        btnSuff.setOnClickListener(v -> {
            musicService.toggleShuffleMode();
            // Update the UI to reflect the shuffle mode status

            updateShuffleButtonUI(musicService.isShuffle());
        });

        btnLoop.setOnClickListener(v -> {
            musicService.toggleLoopMode();
            // Update the UI to reflect the loop mode status
            updateLoopButtonUI(musicService.isLoop());
        });

        btnNext.setOnClickListener(v -> {
            musicService.playNextTrack();
            updatePlayPauseButtonsVisibility(true);
        });

        btnPrev.setOnClickListener(v -> {
            musicService.playPreviousTrack();
            updatePlayPauseButtonsVisibility(true);
        });

        // Set up the seek bar change listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the current time display as the user drags the seek bar thumb
                tvCurTime.setText(musicService.createTimeLabel(progress * 1000));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Handle when the user stops dragging the seek bar thumb
                int newPosition = seekBar.getProgress() * 1000; // Convert to milliseconds
                musicService.seekTo(newPosition); // Send the new position to the MusicService
            }
        });
    }
    private void updateShuffleButtonUI(boolean isShuffle) {
        btnSuff.setBackgroundResource(isShuffle ? R.drawable.shuffle_on : R.drawable.shuffle);
    }

    private void updateLoopButtonUI(int isLoop) {
        if (isLoop==1) btnLoop.setBackgroundResource(R.drawable.repeat_one_on);
        else if (isLoop==2) btnLoop.setBackgroundResource(R.drawable.repeat_on);
        else btnLoop.setBackgroundResource(R.drawable.repeat);
    }

    private void updatePlayPauseButtonsVisibility(boolean isPlaying) {
        btnPlay.setBackgroundResource(isPlaying ? R.drawable.pause : R.drawable.play);
        animationControl(isPlaying);
    }
    private void loadPlayingSongFromService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
                musicService = binder.getService();
                isBound = true;

                // Set the MusicActivity as the listener for song changes
                musicService.setOnSongChangedListener(MusicActivity.this);

                Song currentSong = musicService.getCurrentPlayingSong();
                if (currentSong != null) {
                    songPlaying = currentSong;
                    setValueForWidgets();
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
            if (Objects.equals(songPlaying.getTitle(), "") || songPlaying.getTitle()==null) {
                btnPlay.setOnClickListener(v -> Toast.makeText(v.getContext(),"Nothing to play!", Toast.LENGTH_SHORT).show());
                btnNext.setOnClickListener(v -> Toast.makeText(v.getContext(),"Nothing to next!", Toast.LENGTH_SHORT).show());
                btnPrev.setOnClickListener(v -> Toast.makeText(v.getContext(),"Nothing to prev!", Toast.LENGTH_SHORT).show());
                btnSuff.setOnClickListener(v -> Toast.makeText(v.getContext(),"Please choose song to play!", Toast.LENGTH_SHORT).show());
                btnLoop.setOnClickListener(v -> Toast.makeText(v.getContext(),"Please choose song to play!", Toast.LENGTH_SHORT).show());
            } else {
                Picasso.get().load(songPlaying.getImgSong()).into(imgSong);
                tvTitle.setText(songPlaying.getTitle());
                tvArtist.setText(songPlaying.getArtist());
                tvAlbum.setText(songPlaying.getAlbum());
                seekBar.setMax(songPlaying.getLength());
            }
        }
        updatePlayPauseButtonsVisibility(musicService.isPlaying());
        updateShuffleButtonUI(musicService.isShuffle());
        updateLoopButtonUI(musicService.isLoop());
    }
    private void updateSeekBarPosition(int currentPosition) {
        seekBar.setProgress(currentPosition/1000);
        tvCurTime.setText(musicService.createTimeLabel(currentPosition));
        tvTotalTime.setText(musicService.createTimeLabel(musicService.getDuration()));
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
        unregisterReceiver(seekBarReceiver);
    }
    @Override
    public void onSongChanged(Song newSong) {
        songPlaying = newSong;
        Log.e("NEXT SONG TEST", "onSongChanged is running!");
        updatePlayPauseButtonsVisibility(true);
        updateShuffleButtonUI(musicService.isShuffle());
        updateLoopButtonUI(musicService.isLoop());
        setValueForWidgets();
    }

    private void animationControl(boolean isPlaying) {
        if(isPlaying) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    imgSong.animate().rotationBy(360).withEndAction(this).setDuration(10000)
                            .setInterpolator(new LinearInterpolator()).start();
                    imgDisk.animate().rotationBy(360).withEndAction(this).setDuration(10000)
                            .setInterpolator(new LinearInterpolator()).start();
                }
            };
            imgSong.animate().rotationBy(360).withEndAction(runnable).setDuration(10000)
                    .setInterpolator(new LinearInterpolator()).start();
            imgDisk.animate().rotationBy(360).withEndAction(runnable).setDuration(10000)
                    .setInterpolator(new LinearInterpolator()).start();
        }else {
            imgSong.animate().cancel();
            imgDisk.animate().cancel();
        }
    }
}
