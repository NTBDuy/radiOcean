package com.duy.radiocean.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duy.radiocean.R;
import com.duy.radiocean.RecyclerViewInterface;
import com.duy.radiocean.model.Song;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListSongAdapter extends RecyclerView.Adapter<ListSongAdapter.ViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<Song> items;

    public ListSongAdapter(Context context, ArrayList<Song> items, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.items = items;
        this.recyclerViewInterface = recyclerViewInterface;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_song, parent, false), recyclerViewInterface);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.artist.setText(items.get(position).getArtist());
        holder.time.setText(createTimeLabel((items.get(position).getLength())*1000));
        Picasso.get().load(items.get(position).getImgSong()).into(holder.img);
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title, artist, time;
        ImageView img;

        public ViewHolder(View inflate, RecyclerViewInterface recyclerViewInterface) {
            super(inflate);

            title = inflate.findViewById(R.id.txtNameSong);
            artist = inflate.findViewById(R.id.txtSinger);
            time = inflate.findViewById(R.id.txtTime);
            img = inflate.findViewById(R.id.imgSong);

            inflate.setOnClickListener(v -> {
                if (recyclerViewInterface != null) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos);
                    }
                }
            });
        }
    }
    public String createTimeLabel(int duration) {
        String timerLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        timerLabel += min + ":";
        if (sec < 10) timerLabel += "0";
        timerLabel += sec;
        return timerLabel;
    }
}
