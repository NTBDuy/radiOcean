package com.duy.radiocean.fragment;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duy.radiocean.R;
import com.duy.radiocean.service.MusicService;
import com.duy.radiocean.model.Song;

public class NowPlayingFragment extends Fragment {
    private ImageView imgSongPlaying;
    private TextView txtNameSongPlaying;
    private TextView txtSingerPlaying;
    private Button btnPlay;
    private Button btnPause;
    private MusicService musicService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_now_playing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        initWidgets();
//        setButtonClickListeners();
    }

    private void initWidgets() {
        btnPlay = requireActivity().findViewById(R.id.btnPlay);
        btnPause = requireActivity().findViewById(R.id.btnPause);
        imgSongPlaying = requireActivity().findViewById(R.id.imgSongPlaying);
        txtNameSongPlaying = requireActivity().findViewById(R.id.txtNameSongPlaying);
        txtSingerPlaying = requireActivity().findViewById(R.id.txtSingerPlaying);
    }

    private void setButtonClickListeners() {
        btnPlay.setOnClickListener(v -> {
            Log.d("NOW PLAYING", "You are click on " + v.getClass());
        });
        btnPause.setOnClickListener(v -> {
            Log.d("NOW PLAYING", "You are click on " + v.getClass());
        });
    }
}
