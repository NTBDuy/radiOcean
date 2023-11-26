package com.duy.radiocean.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.duy.radiocean.R;
import com.duy.radiocean.activity.MusicActivity;
import com.duy.radiocean.model.Song;
import com.duy.radiocean.notification.MusicNotification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MusicService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private Song songPlaying;
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;
    private int currentSong = 0, totalSong = 0, playbackPosition = 0;
    private ArrayList<Song> songList;
    private ArrayList<Song> shuffledSongList;
    private boolean isShuffleMode = false;
    private int loopMode = LOOP_NONE; // Added loop mode
    private final String TAG = "SERVICE HERE";
    private final Handler handler = new Handler();

    public static final int LOOP_NONE = 0;
    public static final int LOOP_ONE = 1;
    public static final int LOOP_ALL = 2;

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

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        songPlaying = new Song();
        mediaPlayer.setOnCompletionListener(mediaPlayer -> playNextTrack());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case "PLAY":
                    if (playbackPosition >= 1) {
                        continueMusic();
                    } else {
                        Song selectedSong = (Song) intent.getSerializableExtra("SONG_INFO");
                        currentSong = intent.getIntExtra("POSITION", 0);
                        playMusic(selectedSong);
                    }
                    startUpdatingSeekBar();
                    break;
                case "PAUSE":
                    pauseMusic();
                    break;
                case "STOP":
                    stopMusic();
                    break;
                case "RESET":
                    resetMusic();
                    break;
                case "LOAD_DATA":
                    songList = (ArrayList<Song>) intent.getSerializableExtra("SONG_LIST");
                    if (songList != null) {
                        totalSong = songList.size();
                    }
                    break;
                case "NEXT":
                    playNextTrack();
                    break;
                case "PREVIOUS":
                    playPreviousTrack();
                    break;
                case "TOGGLE_SHUFFLE":
                    toggleShuffleMode();
                    break;
                case "TOGGLE_LOOP":
                    toggleLoopMode();
                    break;
            }
        }
        return START_STICKY;
    }

    @SuppressLint("ResourceAsColor")
    private void updateNotiofication() {
        Bitmap bitmap = BitmapFactory.decodeFile(songPlaying.getImgSong());
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Create an Intent for the activity you want to start.
        Intent resultIntent = new Intent(this, MusicActivity.class);
// Create the TaskStackBuilder and add the intent, which inflates the back
// stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack.
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(1,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

//        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.layout_notification);
//        notificationLayout.setTextViewText(R.id.TvNotificationTitle, songPlaying.getTitle());
//        notificationLayout.setTextViewText(R.id.TvNotificationArtist, songPlaying.getArtist());
//
//        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.layout_notification_expanded);
//        notificationLayoutExpanded.setTextViewText(R.id.TvNotificationTitleExpanded, songPlaying.getTitle());
//        notificationLayoutExpanded.setTextViewText(R.id.TvNotificationArtistExpanded, songPlaying.getArtist());

        notificationBuilder = new NotificationCompat.Builder(this, MusicNotification.ChannelId)
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setSubText("now playing")
                .setSmallIcon(R.drawable.music)
                .setSound(uri)
                .setAutoCancel(true)
                .setContentTitle(songPlaying.getTitle())
                .setContentText(songPlaying.getArtist())
                .setLargeIcon(bitmap)
//                .setCustomContentView(notificationLayout)
//                .setCustomBigContentView(notificationLayoutExpanded)
                .setContentIntent(resultPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1)
                );

        if (mediaPlayer.isPlaying()) {
            notificationBuilder.addAction(R.drawable.backward, "prev", null)
                    .addAction(R.drawable.play, "pause", null)
                    .addAction(R.drawable.forward, "next", null);
        } else {
            notificationBuilder.addAction(R.drawable.backward, "prev", null)
                    .addAction(R.drawable.pause, "pause", null)
                    .addAction(R.drawable.forward, "next", null);
        }

        Notification notification = notificationBuilder.build();

        sendNotification(notification);
    }

    private void sendNotification(Notification notification) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Log.e(TAG, "success");
            notificationManager.notify(1,notification);
        }
    }


    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public boolean isShuffle() {
        return isShuffleMode;
    }

    public int isLoop() {
        return loopMode;
    }

    public void playMusic(Song song) {
        Log.d(TAG, "PLAY");
        if (song != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(song.getLink());
                mediaPlayer.prepare();
                songPlaying = song;
                notifySongChanged(song);
                mediaPlayer.start();
            } catch (IOException e) {
                Log.e(TAG, "Error while playing music: " + e.getMessage());
            }
        }
    }

    public void resetMusic() {
        Log.d(TAG, "RESET");
        mediaPlayer.reset();
    }

    public void continueMusic() {
        Log.d(TAG, "CONTINUE");
        if (playbackPosition >= 1) {
            Log.d(TAG, "Continue this song!");
            mediaPlayer.seekTo(playbackPosition);
            mediaPlayer.start();
        } else {
            Log.d(TAG, "Nothing to continue");
            mediaPlayer.reset();
        }
    }

    public void pauseMusic() {
        Log.d(TAG, "PAUSE");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playbackPosition = mediaPlayer.getCurrentPosition();
        }
    }

    public void playNextTrack() {
        if (loopMode == LOOP_ONE) {
            // In LOOP_ONE mode, play the same song again.
            playMusic(songList.get(currentSong));
            return;
        } else if (loopMode == LOOP_ALL) {
            if (isShuffleMode) {
                if (shuffledSongList != null && !shuffledSongList.isEmpty()) {
                    currentSong = (currentSong + 1) % shuffledSongList.size();
                    playMusic(shuffledSongList.get(currentSong));
                    return;
                }
            } else {
                currentSong = (currentSong + 1) % totalSong;
            }
        } else {
            if (isShuffleMode) {
                if (shuffledSongList != null && !shuffledSongList.isEmpty()) {
                    currentSong = (currentSong + 1) % shuffledSongList.size();
                    playMusic(shuffledSongList.get(currentSong));
                    return;
                }
            } else {
                if (currentSong < totalSong - 1) {
                    currentSong++;
                } else {
                    stopMusic();
                    return;
                }
            }
        }
        playMusic(songList.get(currentSong));
    }

    public void playPreviousTrack() {
        if (loopMode == LOOP_ONE) {
            // In LOOP_ONE mode, play the same song again.
            playMusic(songList.get(currentSong));
            return;
        } else if (loopMode == LOOP_ALL) {
            if (isShuffleMode) {
                if (shuffledSongList != null && !shuffledSongList.isEmpty()) {
                    currentSong = (currentSong - 1 + shuffledSongList.size()) % shuffledSongList.size();
                    playMusic(shuffledSongList.get(currentSong));
                    return;
                }
            } else {
                currentSong = (currentSong - 1 + totalSong) % totalSong;
            }
        } else {
            if (isShuffleMode) {
                if (shuffledSongList != null && !shuffledSongList.isEmpty()) {
                    currentSong = (currentSong - 1 + shuffledSongList.size()) % shuffledSongList.size();
                    playMusic(shuffledSongList.get(currentSong));
                    return;
                }
            } else {
                if (currentSong > 0) {
                    currentSong--;
                } else {
                    stopMusic();
                    return;
                }
            }
        }
        playMusic(songList.get(currentSong));
    }


    public void stopMusic() {
        Log.d(TAG, "STOP");
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
        playbackPosition = 0;
    }

    private void notifySongChanged(Song newSong) {
        if (songChangedListener != null) {
            songChangedListener.onSongChanged(newSong);
            updateNotiofication();
        }
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public String createTimeLabel(int duration) {
        String timerLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        timerLabel += min + ":";
        if (sec < 10) timerLabel += "0";
        timerLabel += sec;
        return timerLabel;
    }

    private void startUpdatingSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentPosition = mediaPlayer.getCurrentPosition();
                Intent intent = new Intent("UPDATE_SEEK_BAR");
                intent.putExtra("CURRENT_POSITION", currentPosition);
                sendBroadcast(intent);
                handler.postDelayed(this, 1000); // Update every second (adjust as needed).
            }
        }, 1000); // Delayed start to match the update interval.
    }

    public void toggleShuffleMode() {
        isShuffleMode = !isShuffleMode;
        if (isShuffleMode) {
            shuffledSongList = new ArrayList<>(songList);
            Collections.shuffle(shuffledSongList);
            currentSong = 0;
        } else {
            shuffledSongList = null;
        }
    }

    public void toggleLoopMode() {
        loopMode = (loopMode + 1) % 3; // Cycle through LOOP_NONE, LOOP_ONE, LOOP_ALL
        Log.d(TAG, "LOOP MODE IS: " + loopMode);
    }
}
