package com.duy.radiocean.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Song implements Parcelable {
    private int id;
    private String title;
    private String album;
    private String artist;
    private int length;
    private String link;
    private String imgAlbum;
    private String imgSong;
    private Boolean TopTrendy;

    public Song() {}

    public Song(Parcel in){
        id = in.readInt();
        title =in.readString();
        album = in.readString();
        artist= in.readString();
        length=in.readInt();
        link=in.readString();
        imgAlbum=in.readString();
        imgSong=in.readString();
        TopTrendy=in.readBoolean();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImgAlbum() {
        return imgAlbum;
    }

    public void setImgAlbum(String imgAlbum) {
        this.imgAlbum = imgAlbum;
    }

    public String getImgSong() {
        return imgSong;
    }

    public void setImgSong(String imgSong) {
        this.imgSong = imgSong;
    }

    public Boolean getTopTrendy() {
        return TopTrendy;
    }

    public void setTopTrendy(Boolean topTrendy) {
        this.TopTrendy = topTrendy;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeInt(length);
        dest.writeString(link);
        dest.writeString(imgAlbum);
        dest.writeString(imgSong);
        dest.writeBoolean(TopTrendy);

    }
}
