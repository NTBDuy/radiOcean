package com.duy.radiocean.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duy.radiocean.R;
import com.duy.radiocean.RecyclerViewInterface;
import com.duy.radiocean.adapter.SongAdapter;
import com.duy.radiocean.model.Song;
import com.duy.radiocean.service.MusicService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SearchFragment extends Fragment implements RecyclerViewInterface, ServiceConnection {
    private SongAdapter adapter;
    private ArrayList<Song> lstSong, tempLst;
    private MusicService musicService;
    private boolean isServiceBound = false;
    ArrayList<Song> filteredList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Bind to the MusicService
        requireActivity().bindService(new Intent(getActivity(), MusicService.class), this, Context.BIND_AUTO_CREATE);
    }
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
    }
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unbind from the MusicService when the fragment is destroyed
        if (isServiceBound) {
            requireActivity().unbindService(this);
            isServiceBound = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        RecyclerView songRV = view.findViewById(R.id.idRVSongs);
        getDataFromDatabase();
        songRV.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new SongAdapter(getActivity(), lstSong, this);
        songRV.setAdapter(adapter);

        return view;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.actionSearch).getActionView();

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filter(newText);
                    return false;
                }
            });
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void filter(String text) {
        filteredList = new ArrayList<>();
        tempLst = filteredList;
        for (Song item : lstSong) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    private void getDataFromDatabase() {
        lstSong = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("song").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    lstSong.add(snapshot.getValue(Song.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SearchFragment", "Failed to read value.", databaseError.toException());
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        if (isServiceBound) {
            Log.e(null, "onItemClick: true" );
            Song selectedSong = tempLst.get(position);
            Intent putIntent = new Intent(getContext(), MusicService.class);
            putIntent.putParcelableArrayListExtra("listSongClicked",filteredList);
            putIntent.putExtra("songClicked",selectedSong);
            putIntent.putExtra("action_music_service",2);
            requireActivity().startService(putIntent);
        } else {
            Log.e("SearchFragment", "MusicService is not bound");
        }
    }

    @Override
    public void onAlbumClick(int position) {
        // Handle album click if needed
    }

    // Implement ServiceConnection methods
    @Override
    public void onServiceConnected(ComponentName name, android.os.IBinder service) {
        musicService = ((MusicService.LocalBinder) service).getService();
        isServiceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
        isServiceBound = false;
    }
}
