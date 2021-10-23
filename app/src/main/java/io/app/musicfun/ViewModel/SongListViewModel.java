package io.app.musicfun.ViewModel;


import androidx.lifecycle.ViewModel;

import java.util.List;

import io.app.musicfun.Models.Songs;

public class SongListViewModel extends ViewModel{
List<Songs> songsList;
boolean shuffleSong;

    public boolean isShuffleSong() {
        return shuffleSong;
    }

    public void setShuffleSong(boolean shuffleSong) {
        this.shuffleSong = shuffleSong;
    }

    public boolean isLoopingSong() {
        return loopingSong;
    }

    public void setLoopingSong(boolean loopingSong) {
        this.loopingSong = loopingSong;
    }

    boolean loopingSong;

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
