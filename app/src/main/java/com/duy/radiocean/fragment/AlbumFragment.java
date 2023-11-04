package com.duy.radiocean.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.duy.radiocean.R;
import com.duy.radiocean.RecyclerViewInterface;
import com.duy.radiocean.adapter.ListSongAdapter;
import com.duy.radiocean.model.Album;
import com.duy.radiocean.model.SharedViewModel;
import com.duy.radiocean.model.Song;
import com.duy.radiocean.service.MusicService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AlbumFragment extends Fragment implements RecyclerViewInterface, MusicService.OnSongChangedListener {
    private ImageView btnBack, imgAlbum1, imgAlbum2, imgSongPlaying;
    private Button btnPlay, btnPause, btnPlayInAlbum, btnPauseInAlbum;
    private SharedViewModel sharedViewModel;
    private Intent serviceIntent;
    private boolean mBound = false;
    private MusicService mService;
    private Album album;
    private TextView txtNameAlbum, tvTitleSongPlaying, tvArtisSongPlaying;
    private RecyclerView rvSong;
    private ListSongAdapter songAdapter;
    private final ArrayList<Song> lstSong = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidgets();
        setButtonClickListeners();
        rvSong.setLayoutManager(new LinearLayoutManager(view.getContext()));
        album = sharedViewModel.getSelectedAlbum();

        Log.d("TEST ALBUM", album.getAlbum());

        loadData();
        getPlayingSongDetail();
    }

    private void initWidgets() {
        btnBack = requireActivity().findViewById(R.id.btnBackFromAlbum);
        imgAlbum1 = requireActivity().findViewById(R.id.imgAlbum1);
        imgAlbum2 = requireActivity().findViewById(R.id.imgAlbum2);
        btnPlay = requireActivity().findViewById(R.id.btnPlay);
        btnPause = requireActivity().findViewById(R.id.btnPause);
        btnPlayInAlbum = requireActivity().findViewById(R.id.btnPlayInAlbum);
        btnPauseInAlbum = requireActivity().findViewById(R.id.btnPauseInAlbum);
        txtNameAlbum = requireActivity().findViewById(R.id.TxtNamePlaylist);
        rvSong = requireActivity().findViewById(R.id.rvListSongFromAlbum);
        tvTitleSongPlaying = requireActivity().findViewById(R.id.txtNameSongPlaying);
        tvArtisSongPlaying = requireActivity().findViewById(R.id.txtSingerPlaying);
        imgSongPlaying = requireActivity().findViewById(R.id.imgSongPlaying);

        imgAlbum2.setRenderEffect(RenderEffect.createBlurEffect(10, 10, Shader.TileMode.MIRROR));
    }

    private void setButtonClickListeners() {
        btnBack.setOnClickListener(v -> {
            replaceFragment(new HomeFragment());
        });
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

        btnPlayInAlbum.setOnClickListener(v -> {

        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    private void loadData() {
        txtNameAlbum.setText(album.getAlbum());
        Picasso.get().load(album.getImgAlbum()).into(imgAlbum1);
        Picasso.get().load(album.getImgAlbum()).into(imgAlbum2);
        getData();
        rvSong.setAdapter(songAdapter);
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onAlbumClick(int position) {

    }

    private void getData() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("song");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lstSong.clear(); // Xóa dữ liệu cũ (nếu có) trong ArrayList
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Song song = snapshot.getValue(Song.class);
                    if (Objects.equals(song.getAlbum(), album.getAlbum())) lstSong.add(song);
                }
                songAdapter = new ListSongAdapter(getActivity(), lstSong, AlbumFragment.this);
                rvSong.setAdapter(songAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getPlayingSongDetail() {
        if (mService != null) {
            Song song = mService.getCurrentPlayingSong();
            tvTitleSongPlaying.setText(song.getTitle());
            tvArtisSongPlaying.setText(song.getArtist());
            Picasso.get().load(song.getImgSong()).into(imgSongPlaying);
            Log.d("PLAYING SONG", "TITLE IS: " + song.getTitle());
        }
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
            mService.setOnSongChangedListener(AlbumFragment.this);
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