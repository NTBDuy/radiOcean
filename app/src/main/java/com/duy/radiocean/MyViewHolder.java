package com.duy.radiocean;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView imgSong;
    TextView idSong, nameSong, singer, time;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        imgSong = itemView.findViewById(R.id.imgSong);
        idSong = itemView.findViewById(R.id.txtIdSong);
        nameSong = itemView.findViewById(R.id.txtNameSong);
        singer = itemView.findViewById(R.id.txtSinger);
        time = itemView.findViewById(R.id.txtTime);
    }
}
