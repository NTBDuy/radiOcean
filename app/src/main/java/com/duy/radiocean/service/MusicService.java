package com.duy.radiocean.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.SeekBar;
import android.widget.TextView;

import com.duy.radiocean.model.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private Song songPlaying;
    private boolean isPlaying = false;
    private int currentSong = 0, totalSong = 0, playbackPosition = 0;
    private ArrayList<Song> songList;
    private SeekBar mSeekBar;
    private TextView mTotalDuration;
    private TextView mCurrentPosition;

    public interface OnSongChangedListener {
        void onSongChanged(Song newSong);
    }

    private OnSongChangedListener songChangedListener;

    public void setOnSongChangedListener(OnSongChangedListener listener) {
        this.songChangedListener = listener;
    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public Song getCurrentPlayingSong() {
        return songPlaying;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        songPlaying = new Song();
        mediaPlayer.setOnCompletionListener(mp -> playNextTrack());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case "PLAY":
                    if (playbackPosition >= 1) {
                        mediaPlayer.start();
                    } else {
                        Song selectedSong = (Song) intent.getSerializableExtra("SONG_INFO");
                        currentSong = intent.getIntExtra("POSITION", 0);
                        playMusic(selectedSong);
                    }
                    break;
                case "PAUSE":
                    pauseMusic();
                    break;
                case "STOP":
                    stopMusic();
                    break;
                case "LOAD_DATA":
                    songList = (ArrayList<Song>) intent.getSerializableExtra("SONG_LIST");
                    totalSong = songList.size();
                    break;
                case "NEXT":
                    playNextTrack();
                    break;
                case "PREVIOUS":
                    playPreviousTrack();
                    break;
            }
        }
        return START_STICKY;
    }

    public void playMusic(Song song) {
        if (song != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(song.getLink());
                mediaPlayer.prepare();
                songPlaying = song;
                notifySongChanged(song);
                mediaPlayer.start();
                isPlaying = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playbackPosition = mediaPlayer.getCurrentPosition();
        }
        isPlaying = false;
    }

    public void continueSong() {
        if (playbackPosition >= 1) {
            mediaPlayer.start();
        }
    }

    private void playNextTrack() {
        if (currentSong < totalSong - 1) {
            currentSong++;
        } else {
            currentSong = 0;
        }
        playMusic(songList.get(currentSong));
    }

    private void playPreviousTrack() {
        if (currentSong > 0) {
            currentSong--;
        } else {
            currentSong = totalSong - 1;
        }
        playMusic(songList.get(currentSong));
    }

    private void stopMusic() {
        mediaPlayer.reset();
        playbackPosition = 0;
        isPlaying = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private void notifySongChanged(Song newSong) {
        if (songChangedListener != null) {
            songChangedListener.onSongChanged(newSong);
        }
    }

    public void loadUIControls(SeekBar seekBar, TextView currentPosition, TextView totalDuration) {
        setUIControls(seekBar, currentPosition, totalDuration);
    }

    public void setUIControls(SeekBar seekBar, TextView currentPosition, TextView totalDuration) {
        mSeekBar = seekBar;
        mCurrentPosition = currentPosition;
        mTotalDuration = totalDuration;

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                mCurrentPosition.setText(createTimeLabel(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        if (isPlaying) {
            mSeekBar.postDelayed(mProgressRunner, 1000);
        }
    }

    private final Runnable mProgressRunner = new Runnable() {
        @Override
        public void run() {
            if (mSeekBar != null) {
                mSeekBar.setProgress(mediaPlayer.getCurrentPosition());

                if (mediaPlayer.isPlaying()) {
                    mSeekBar.postDelayed(mProgressRunner, 1000);
                }
            }
        }
    };

    public String createTimeLabel(int duration) {
        String timerLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        timerLabel += min + ":";
        if (sec < 10) timerLabel += "0";
        timerLabel += sec;
        return timerLabel;
    }
}
