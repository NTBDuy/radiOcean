package com.duy.radiocean.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private Handler seekBarHandler;
    private Song songPlaying = new Song();

    private final BroadcastReceiver seekBarReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("UPDATE_SEEK_BAR")) {
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
        btnBackFromMusicAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService.isPlaying()) {
                    musicService.pauseMusic();
                    updatePlayPauseButtonsVisibility(false);
                } else {
                    musicService.continueMusic();
                    updatePlayPauseButtonsVisibility(true);
                }
            }
        });

        btnSuff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.toggleShuffleMode();
                // Update the UI to reflect the shuffle mode status
//                updateShuffleButtonUI();
            }
        });

        btnLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.toggleLoopMode();
                // Update the UI to reflect the loop mode status
//                updateLoopButtonUI();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playNextTrack();
                updatePlayPauseButtonsVisibility(true);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playPreviousTrack();
                updatePlayPauseButtonsVisibility(true);
            }
        });

        seekBarHandler = new Handler();
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
    private void updatePlayPauseButtonsVisibility(boolean isPlaying) {
        if (isPlaying) {
            btnPlay.setBackgroundResource(R.drawable.pause);
            animationControl(isPlaying);
        } else {
            btnPlay.setBackgroundResource(R.drawable.play);
            animationControl(isPlaying);
        }
//        btnPlay.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
//        btnPause.setVisibility(isPlaying ? View.VISIBLE : View.GONE);
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
                btnPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(),"Nothing to play!", Toast.LENGTH_SHORT).show();
                    }
                });
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(),"Nothing to next!", Toast.LENGTH_SHORT).show();
                    }
                });
                btnPrev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(),"Nothing to prev!", Toast.LENGTH_SHORT).show();
                    }
                });

//                btnPause.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(v.getContext(),"Nothing to pause!", Toast.LENGTH_SHORT).show();
//                    }
//                });
            } else {
                Picasso.get().load(songPlaying.getImgSong()).into(imgSong);
                tvTitle.setText(songPlaying.getTitle());
                tvArtist.setText(songPlaying.getArtist());
                tvAlbum.setText(songPlaying.getAlbum());
                seekBar.setMax(songPlaying.getLength());
            }
        }
        updatePlayPauseButtonsVisibility(musicService.isPlaying());
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
