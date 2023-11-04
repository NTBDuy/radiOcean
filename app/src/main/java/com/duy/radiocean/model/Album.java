package com.duy.radiocean.model;

public class Album {
    private String album;
    private String imgAlbum;

    public Album() {}

    public Album(String album, String imgAlbum) {
        this.album = album;
        this.imgAlbum = imgAlbum;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getImgAlbum() {
        return imgAlbum;
    }

    public void setImgAlbum(String imgAlbum) {
        this.imgAlbum = imgAlbum;
    }
}
