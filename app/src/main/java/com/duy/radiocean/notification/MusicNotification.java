package com.duy.radiocean.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.duy.radiocean.R;

public class MusicNotification extends Application {

    public static final String ChannelId = "Music_Channel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(ChannelId, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
