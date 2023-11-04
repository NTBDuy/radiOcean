package com.duy.radiocean.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import com.duy.radiocean.adapter.ListAlbumAdapter;
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
import java.util.Set;

public class HomeFragment extends Fragment implements RecyclerViewInterface, MusicService.OnSongChangedListener {
    private RecyclerView rvSong, rvAlbum;
    private Button btnPlay, btnPause;
    private TextView tvTitleSongPlaying, tvArtisSongPlaying;
    private ImageView imgSongPlaying;
    private ListSongAdapter songAdapter;
    private ListAlbumAdapter albumAdapter;
    private Intent serviceIntent;
    private MusicService mService;
    private SharedViewModel sharedViewModel;
    private boolean mBound = false;
    private final ArrayList<Song> lstSong = new ArrayList<>();
    private final ArrayList<Album> lstAlbum = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view,
                  @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidgets();
        rvSong.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvAlbum.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        serviceIntent = new Intent(requireContext(), MusicService.class);
        loadData();
        setButtonClickListeners();
        getPlayingSongDetail();
    }

    private void initWidgets() {
        rvSong = requireActivity().findViewById(R.id.recyclerViewListSong);
        rvAlbum = requireActivity().findViewById(R.id.recyclerViewAlbum);
        btnPlay = requireActivity().findViewById(R.id.btnPlay);
        btnPause = requireActivity().findViewById(R.id.btnPause);
        tvTitleSongPlaying = requireActivity().findViewById(R.id.txtNameSongPlaying);
        tvArtisSongPlaying = requireActivity().findViewById(R.id.txtSingerPlaying);
        imgSongPlaying = requireActivity().findViewById(R.id.imgSongPlaying);
    }
    private void setButtonClickListeners() {
        btnPlay.setOnClickListener(v -> {
            requireActivity().startService(createPlayIntent(0));
            updatePlayPauseButtonsVisibility(true);
        });
        btnPause.setOnClickListener(v -> {
            requireActivity().startService(serviceIntent.setAction("PAUSE"));
            updatePlayPauseButtonsVisibility(false);
        });
    }
    private void loadData() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("song");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lstSong.clear(); // Xóa dữ liệu cũ (nếu có) trong ArrayList
                Set<String> uniqueAlbums = new HashSet<>(); // Sử dụng một tập hợp để theo dõi album không trùng lặp
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Song song = snapshot.getValue(Song.class);
                    if (uniqueAlbums.add(song.getAlbum())) { // Kiểm tra xem album đã được thêm chưa
                        Album album = new Album();
                        album.setAlbum(song.getAlbum());
                        album.setImgAlbum(song.getImgAlbum());
                        lstAlbum.add(album); // Thêm album vào danh sách album
                        Log.d("ALBUM ITEM", album.getAlbum());
                    }
                    if (song.getTopTrendy()) lstSong.add(song);
                }
                songAdapter = new ListSongAdapter(getActivity(), lstSong, HomeFragment.this);
                rvSong.setAdapter(songAdapter);
                albumAdapter = new ListAlbumAdapter(getActivity(), lstAlbum, HomeFragment.this);
                rvAlbum.setAdapter(albumAdapter);
                putDataToService(lstSong);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    public void onItemClick(int position) {
        Log.d("CLICK EVENT", "You click in " + position);
        requireActivity().startService(serviceIntent.setAction("STOP"));
        requireActivity().startService(createPlayIntent(position));
        updatePlayPauseButtonsVisibility(true);
    }

    @Override
    public void onAlbumClick(int position) {
        Album album = lstAlbum.get(position);
        sharedViewModel.setSelectedAlbum(album);
        // Chuyển sang AlbumFragment
        replaceFragment(new AlbumFragment());
    }

    private Intent createPlayIntent(int position) {
        Intent playIntent = new Intent(getActivity(), MusicService.class);
        playIntent.setAction("PLAY");
        playIntent.putExtra("POSITION", position);
        playIntent.putExtra("SONG_INFO", lstSong.get(position));
        return playIntent;
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
        Intent intent = new Intent(getActivity(), MusicService.class);
        requireActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    private void unbindMusicService() {
        if (mBound) {
            requireActivity().unbindService(mConnection);
            mBound = false;
        }
    }
    private void putDataToService(ArrayList<Song> songList) {
        Intent putIntent = new Intent(getActivity(), MusicService.class);
        putIntent.putExtra("SONG_LIST", songList);
        requireActivity().startService(putIntent.setAction("LOAD_DATA"));
    }
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            mService.setOnSongChangedListener(HomeFragment.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    private void updatePlayPauseButtonsVisibility(boolean isPlaying) {
        btnPlay.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
        btnPause.setVisibility(isPlaying ? View.VISIBLE : View.GONE);
    }
    @Override
    public void onSongChanged(Song newSong) {
        getPlayingSongDetail();
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
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }
}