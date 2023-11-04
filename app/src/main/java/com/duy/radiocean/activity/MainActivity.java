package com.duy.radiocean.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duy.radiocean.R;
import com.duy.radiocean.databinding.ActivityMainBinding;
import com.duy.radiocean.fragment.HomeFragment;
import com.duy.radiocean.fragment.ProfileFragment;
import com.duy.radiocean.fragment.SearchFragment;
import com.duy.radiocean.fragment.NowPlayingFragment;
import com.duy.radiocean.model.Song;
import com.duy.radiocean.service.MusicService;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private NowPlayingFragment nowPlayingFragment;
    RelativeLayout relativeLayout;
    Button btnPlay, btnPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo nowPlayingFragment và hiển thị nó trong now_playing_container
        nowPlayingFragment = new NowPlayingFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.now_playing_container, nowPlayingFragment)
                .commit();

        replaceFragment(new HomeFragment());

        binding.bottomNavigation.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (item.getItemId() == R.id.search) {
                replaceFragment(new SearchFragment());
            }
        });
        relativeLayout = findViewById(R.id.layoutPlaying);
//        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ALO ALO", "ALO ALO");
                Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        btnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("TEST PLAYING SONG", "You are click on " + getClass());
//            }
//        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }


}
