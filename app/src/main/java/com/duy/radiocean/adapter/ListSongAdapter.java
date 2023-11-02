package com.duy.radiocean.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duy.radiocean.ListSongViewHolder;
import com.duy.radiocean.R;
import com.duy.radiocean.model.Song;

import java.util.List;

public class ListSongAdapter extends RecyclerView.Adapter<ListSongViewHolder> {
    Context context;
    List<Song> items;

    public ListSongAdapter(Context context, List<Song> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ListSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListSongViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_list_song, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListSongViewHolder holder, int position) {
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
