package com.duy.radiocean.model;

public class Song {
    Integer id_song;
    String name_song;
    String singer;
    String time_song;

    int image;

    public Song(Integer id_song, String name_song, String singer, String time_song, int image) {
        this.id_song = id_song;
        this.name_song = name_song;
        this.singer = singer;
        this.time_song = time_song;
        this.image = image;
    }

    public Integer getId_song() {
        return id_song;
    }

    public void setId_song(Integer id_song) {
        this.id_song = id_song;
    }

    public String getName_song() {
        return name_song;
    }

    public void setName_song(String name_song) {
        this.name_song = name_song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getTime_song() {
        return time_song;
    }

    public void setTime_song(String time_song) {
        this.time_song = time_song;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
