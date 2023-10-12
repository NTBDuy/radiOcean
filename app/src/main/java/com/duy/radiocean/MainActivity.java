package com.duy.radiocean;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        List<Song> items = new ArrayList<Song>();
        items.add(new Song(1, "Nhạc hay", "The Big", "4:20", R.drawable.a));
        items.add(new Song(2, "Ca khúc ưa thích", "Ca sĩ nổi tiếng", "3:45", R.drawable.a));
        items.add(new Song(3, "Bản nhạc du dương", "Ban nhạc tự tạo", "5:10", R.drawable.a));
        items.add(new Song(4, "Nhạc ballad cảm động", "Ca sĩ tài năng", "4:55", R.drawable.a));
        items.add(new Song(5, "Âm nhạc phục vụ dân ca", "Ban nhạc dân ca", "3:30", R.drawable.a));
        items.add(new Song(6, "Ca khúc phổ biến", "Ca sĩ nổi tiếng", "3:15", R.drawable.a));
        items.add(new Song(7, "Nhạc rock nổi tiếng", "Ban nhạc rock", "4:40", R.drawable.a));
        items.add(new Song(8, "Bản nhạc jazz thú vị", "Ban nhạc jazz", "6:25", R.drawable.a));
        items.add(new Song(9, "Ca khúc hòa nhạc tinh tế", "Nghệ sĩ hòa nhạc", "7:05", R.drawable.a));
        items.add(new Song(10, "Nhạc dance sôi động", "DJ nổi tiếng", "3:50", R.drawable.a));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(), items));
    }
}