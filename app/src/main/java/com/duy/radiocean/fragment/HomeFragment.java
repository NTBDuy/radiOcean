package com.duy.radiocean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class HomeFragment extends Fragment implements RecyclerViewInterface, MusicService.OnSongChangedListener {
    private RecyclerView rvSong, rvAlbum;
    private ListSongAdapter songAdapter;
    private ListAlbumAdapter albumAdapter;
    private SharedViewModel sharedViewModel;
    private final ArrayList<Song> lstSong = new ArrayList<>();
    private final ArrayList<Album> lstAlbum = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidgets();
        rvSong.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvAlbum.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        getDataFromDatabase();
    }
    private void initWidgets() {
        rvSong = requireActivity().findViewById(R.id.recyclerViewListSong);
        rvAlbum = requireActivity().findViewById(R.id.recyclerViewAlbum);
    }
    private void getDataFromDatabase() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("song");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lstSong.clear(); // Xóa dữ liệu cũ (nếu có) trong ArrayList
                Set<String> uniqueAlbums = new HashSet<>(); // Sử dụng một tập hợp để theo dõi album không trùng lặp
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Song song = snapshot.getValue(Song.class);
                    if (song != null && uniqueAlbums.add(song.getAlbum())) { // Kiểm tra xem album đã được thêm chưa
                        Album album = new Album();
                        album.setAlbum(song.getAlbum());
                        album.setImgAlbum(song.getImgAlbum());
                        lstAlbum.add(album); // Thêm album vào danh sách album
                        Log.d("ALBUM ITEM", album.getAlbum());
                    }
                    if (song != null && song.getTopTrendy()) lstSong.add(song);
                }
                songAdapter = new ListSongAdapter(getActivity(), lstSong, HomeFragment.this);
                rvSong.setAdapter(songAdapter);
                albumAdapter = new ListAlbumAdapter(getActivity(), lstAlbum, HomeFragment.this);
                rvAlbum.setAdapter(albumAdapter);
//                putDataToService(lstSong);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    public void onItemClick(int position) {
        putDataToService(lstSong, position);
    }
    @Override
    public void onAlbumClick(int position) {
        Album album = lstAlbum.get(position);
        sharedViewModel.setSelectedAlbum(album);
        // Chuyển sang AlbumFragment
        replaceFragment(new AlbumFragment());
    }

    private void putDataToService(ArrayList<Song> songList, int position) {
        if(songList!=null){
            Intent putIntent = new Intent(getActivity(), MusicService.class);
            putIntent.putExtra("songPos",position);
            putIntent.putParcelableArrayListExtra("listSongClicked", songList);
            putIntent.putExtra("songClicked",lstSong.get(position));
            int ACTION_PLAY = 2;
            putIntent.putExtra("action_music_service", ACTION_PLAY);
            requireActivity().startService(putIntent);
        }else{
            Log.e(null, "putDataToService: null" );
        }
    }

    @Override
    public void onSongChanged(Song newSong) {}

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

}