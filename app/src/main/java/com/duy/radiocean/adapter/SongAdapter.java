package com.duy.radiocean.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.duy.radiocean.RecyclerViewInterface;
import com.duy.radiocean.R;
import com.duy.radiocean.model.Song;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private ArrayList<Song> modelSongArraylist;
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    public SongAdapter(Context context, ArrayList<Song> songModelArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
        this.modelSongArraylist = songModelArrayList;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<Song> filterlist) {
        modelSongArraylist = filterlist;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.song_rv_items, parent, false), recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song model = modelSongArraylist.get(position);
        holder.songName.setText(model.getTitle());
        holder.artist.setText(model.getArtist());
    }
    @Override
    public int getItemCount() {
        return modelSongArraylist.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songName;
        TextView artist;
        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                if (recyclerViewInterface != null) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos);
                    }
                }
            });

            songName = itemView.findViewById(R.id.SongName);
            artist = itemView.findViewById(R.id.Artist);
        }
    }
}
