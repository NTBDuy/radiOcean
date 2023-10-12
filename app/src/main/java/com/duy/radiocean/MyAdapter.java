package com.duy.radiocean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    Context context;
    List<Song> items;

    public MyAdapter(Context context, List<Song> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.list_song, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.idSong.setText(items.get(position).getId_song().toString());
        holder.imgSong.setImageResource(items.get(position).getImage());
        holder.nameSong.setText(items.get(position).getName_song());
        holder.singer.setText(items.get(position).getSinger());
        holder.time.setText(items.get(position).getTime_song());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
