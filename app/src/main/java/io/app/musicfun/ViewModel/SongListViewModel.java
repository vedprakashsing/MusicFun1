package io.app.musicfun.ViewModel;


import androidx.lifecycle.ViewModel;

import java.util.List;

import io.app.musicfun.Models.Songs;

public class SongListViewModel extends ViewModel{
List<Songs> songsList;

    public SongListViewModel() {
        //to get use
    }

    public List<Songs> getSongsList() {
        return songsList;
    }

    public void setSongsList(List<Songs> songsList) {
        this.songsList = songsList;
    }
}
