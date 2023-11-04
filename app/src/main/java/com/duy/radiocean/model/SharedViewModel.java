package com.duy.radiocean.model;

import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private Album selectedAlbum;

    public void setSelectedAlbum(Album album) {
        selectedAlbum = album;
    }

    public Album getSelectedAlbum() {
        return selectedAlbum;
    }
}