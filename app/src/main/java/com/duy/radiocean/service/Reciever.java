package com.duy.radiocean.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Reciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle recieveBundle ;
        int actionMusic = intent.getIntExtra("action_music",0);


        Intent serviceIntent = new Intent(context, MusicService.class);
        serviceIntent.putExtra("action_music_service",actionMusic);

        context.startService(serviceIntent);
    }
}
