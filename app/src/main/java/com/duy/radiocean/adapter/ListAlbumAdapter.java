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
import com.duy.radiocean.model.Album;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListAlbumAdapter extends RecyclerView.Adapter<ListAlbumAdapter.ViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<Album> items;

    public ListAlbumAdapter(Context context, ArrayList<Album> items, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.items = items;
        this.recyclerViewInterface = recyclerViewInterface;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_album, parent, false), recyclerViewInterface);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(String.valueOf(items.get(position).getAlbum()));
        Picasso.get().load(items.get(position).getImgAlbum()).into(holder.img);
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView name;
        public ViewHolder(View inflate, RecyclerViewInterface recyclerViewInterface) {
            super(inflate);

            name = inflate.findViewById(R.id.txtNameAlbum);
            img = inflate.findViewById(R.id.imgAlbum);

            inflate.setOnClickListener(v -> {
                if (recyclerViewInterface != null) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onAlbumClick(pos);
                    }
                }

            });
        }
    }
}
