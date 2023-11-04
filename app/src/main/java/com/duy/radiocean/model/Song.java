package com.duy.radiocean.model;

import java.io.Serializable;

public class Song implements Serializable {
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

    public Song(int id, String title, String album, String artist, int length, String link, String imgAlbum, String imgSong, Boolean TopTrendy) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.length = length;
        this.link = link;
        this.imgAlbum = imgAlbum;
        this.imgSong = imgSong;
        this.TopTrendy = TopTrendy;
    }

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
}
