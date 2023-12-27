package com.duy.radiocean.fragment;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duy.radiocean.R;
import com.duy.radiocean.RecyclerViewInterface;
import com.duy.radiocean.activity.MainActivity;
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
import java.util.Objects;

public class AlbumFragment extends Fragment implements RecyclerViewInterface, MusicService.OnSongChangedListener {
    private ImageView btnBack, imgAlbum1, imgAlbum2;
    private SharedViewModel sharedViewModel;
    private Album album;
    private TextView txtNameAlbum;
    private RecyclerView rvSong;
    private ListSongAdapter songAdapter;
    private final ArrayList<Song> lstSongInAlbum = new ArrayList<>();
    private static final int ACTION_PLAY = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album, container, false);
    }
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidgets();
        setButtonClickListeners();
        rvSong.setLayoutManager(new LinearLayoutManager(view.getContext()));
        album = sharedViewModel.getSelectedAlbum();
        setDataForWidgets();

    }

    private void initWidgets() {
        btnBack = requireActivity().findViewById(R.id.btnBackFromAlbum);
        imgAlbum1 = requireActivity().findViewById(R.id.imgAlbum1);
        imgAlbum2 = requireActivity().findViewById(R.id.imgAlbum2);
        txtNameAlbum = requireActivity().findViewById(R.id.TxtNamePlaylist);
        rvSong = requireActivity().findViewById(R.id.rvListSongFromAlbum);
        imgAlbum2.setRenderEffect(RenderEffect.createBlurEffect(50, 50, Shader.TileMode.MIRROR));
    }

    private void setButtonClickListeners() {
        btnBack.setOnClickListener(v -> replaceFragment(new HomeFragment()));
    }


    private void setDataForWidgets() {
        txtNameAlbum.setText(album.getAlbum());
        Picasso.get().load(album.getImgAlbum()).into(imgAlbum1);
        Picasso.get().load(album.getImgAlbum()).into(imgAlbum2);
        getDataFromDatabase();
        rvSong.setAdapter(songAdapter);
    }

    private void getDataFromDatabase() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("song");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lstSongInAlbum.clear(); // Xóa dữ liệu cũ (nếu có) trong ArrayList
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Song song = snapshot.getValue(Song.class);
                    if (song != null && Objects.equals(song.getAlbum(), album.getAlbum()))
                        lstSongInAlbum.add(song);
                }
                songAdapter = new ListSongAdapter(getActivity(), lstSongInAlbum, AlbumFragment.this);
                rvSong.setAdapter(songAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onAlbumClick(int position) {
    }

    @Override
    public void onItemClick(int position) {
        putDataToService(lstSongInAlbum,position);

    }

    @Override
    public void onSongChanged(Song newSong) {
    }


    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    private void putDataToService(ArrayList<Song> songList, int position) {
        Intent putIntent = new Intent(getActivity(), MusicService.class);
        putIntent.putExtra("songPos",position);
        putIntent.putParcelableArrayListExtra("listSongClicked", songList);
        putIntent.putExtra("songClicked",songList.get(position));
        putIntent.putExtra("action_music_service",ACTION_PLAY);
        requireActivity().startService(putIntent);
    }

}