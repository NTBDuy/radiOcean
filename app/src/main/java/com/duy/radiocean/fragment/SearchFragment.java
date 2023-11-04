package com.duy.radiocean.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.duy.radiocean.R;
import com.duy.radiocean.model.Song;
import com.duy.radiocean.service.MusicService;

public class SearchFragment extends Fragment implements MusicService.OnSongChangedListener{
    Button btnLogout, btnPlay, btnPause;
    private TextView tvTitleSongPlaying, tvArtisSongPlaying;
    private ImageView imgSongPlaying;
    private Intent serviceIntent;
    private boolean mBound = false;
    private MusicService mService;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
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
            mService.setOnSongChangedListener(SearchFragment.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onSongChanged(Song newSong) {
        // Đây bạn có thể cập nhật giao diện người dùng với thông tin bài hát mới.
    }

    private Song getSongToPlay() {
        // Điền vào logic để lấy ra bài hát bạn muốn phát ở đây.
        return null;
    }
}