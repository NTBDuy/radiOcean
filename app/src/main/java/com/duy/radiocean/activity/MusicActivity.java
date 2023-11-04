package com.duy.radiocean.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.duy.radiocean.R;
import com.squareup.picasso.Picasso;

public class MusicActivity extends AppCompatActivity {
    private ImageView imgSong;
    private TextView tvTitle, tvArtist, tvAlbum, tvCurrentTime, tvTotalTime;
    private Button btnPlay, btnBack;
    private String urlSong = "";
    private SeekBar skBar;

    private Intent musicServiceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
//
//
//        Picasso.get().load(getIntent().getStringExtra("imgSong")).into(imgSong);
//        tvTitle.setText(getIntent().getStringExtra("title"));
//        tvArtist.setText(getIntent().getStringExtra("artist"));
//        tvAlbum.setText(getIntent().getStringExtra("album"));
//        urlSong = getIntent().getStringExtra("link");
    }

    private void initWidgets() {
        skBar = findViewById(R.id.skbSongTime);
        imgSong = findViewById(R.id.ivSong);
        tvTitle = findViewById(R.id.tvSongTitle);
        tvArtist = findViewById(R.id.tvSongArtist);
        tvAlbum = findViewById(R.id.tvSongAlbum);
        tvCurrentTime = findViewById(R.id.tvTimeCurrent);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        btnBack = findViewById(R.id.btnBack);
        btnPlay = findViewById(R.id.btnPlay);
    }
}
