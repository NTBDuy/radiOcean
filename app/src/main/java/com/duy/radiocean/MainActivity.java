package com.duy.radiocean;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.duy.radiocean.adapter.AlbumSongAdapter;

import java.util.ArrayList;


import com.duy.radiocean.databinding.ActivityMainBinding;
import com.duy.radiocean.fragment.HomeFragment;
import com.duy.radiocean.fragment.ProfileFragment;
import com.duy.radiocean.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getImages();
    }

    private void getImages(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps");
        mImageUrls.add("https://i.pinimg.com/564x/2e/a0/1a/2ea01a8f3eed75cc20edf64f217fae00.jpg");
        mNames.add("Album 1");
        mImageUrls.add("https://i.pinimg.com/736x/96/4a/de/964ade8967fec3aaaf38d2b39a0ae040.jpg");
        mNames.add("Album 1");
        mImageUrls.add("https://i.pinimg.com/564x/2e/a0/1a/2ea01a8f3eed75cc20edf64f217fae00.jpg");
        mNames.add("Album 1");
        mImageUrls.add("https://i.pinimg.com/736x/96/4a/de/964ade8967fec3aaaf38d2b39a0ae040.jpg");
        mNames.add("Album 1");
        mImageUrls.add("https://i.pinimg.com/564x/4f/86/77/4f867765bba422b17e3b450af42e7df1.jpg");
        mNames.add("Album 1");
        mImageUrls.add("https://i.pinimg.com/564x/4f/86/77/4f867765bba422b17e3b450af42e7df1.jpg");
        mNames.add("Album 1");
        mImageUrls.add("https://i.pinimg.com/564x/4f/86/77/4f867765bba422b17e3b450af42e7df1.jpg");
        mNames.add("Album 1");

        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recycler view");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        AlbumSongAdapter adapter = new AlbumSongAdapter(this, mNames, mImageUrls);
        recyclerView.setAdapter(adapter);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomeNav.setOnItemReselectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (item.getItemId() == R.id.search) {
                replaceFragment(new SearchFragment());
            };
        });

//        loadListSong();
    }

     // Load danh sach nhac len recyclerView
//    void loadListSong() {
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//
//        List<Song> items = new ArrayList<Song>();
//        items.add(new Song(1, "Nhạc hay", "The Big", "4:20", R.drawable.a));
//        items.add(new Song(2, "Ca khúc ưa thích", "Ca sĩ nổi tiếng", "3:45", R.drawable.a));
//        items.add(new Song(3, "Bản nhạc du dương", "Ban nhạc tự tạo", "5:10", R.drawable.a));
//        items.add(new Song(4, "Nhạc ballad cảm động", "Ca sĩ tài năng", "4:55", R.drawable.a));
//        items.add(new Song(5, "Âm nhạc phục vụ dân ca", "Ban nhạc dân ca", "3:30", R.drawable.a));
//        items.add(new Song(6, "Ca khúc phổ biến", "Ca sĩ nổi tiếng", "3:15", R.drawable.a));
//        items.add(new Song(7, "Nhạc rock nổi tiếng", "Ban nhạc rock", "4:40", R.drawable.a));
//        items.add(new Song(8, "Bản nhạc jazz thú vị", "Ban nhạc jazz", "6:25", R.drawable.a));
//        items.add(new Song(9, "Ca khúc hòa nhạc tinh tế", "Nghệ sĩ hòa nhạc", "7:05", R.drawable.a));
//        items.add(new Song(10, "Nhạc dance sôi động", "DJ nổi tiếng", "3:50", R.drawable.a));
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new ListSongAdapter(getApplicationContext(), items));
//    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
}

