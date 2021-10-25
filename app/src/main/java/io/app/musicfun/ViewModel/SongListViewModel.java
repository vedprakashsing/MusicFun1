package io.app.musicfun.ViewModel;


import android.media.MediaPlayer;

import androidx.lifecycle.ViewModel;

import java.util.List;

import io.app.musicfun.Models.Songs;

public class SongListViewModel extends ViewModel{
List<Songs> songsList;
boolean shuffleSong;
MediaPlayer mediaPlayer;
boolean loopingSong;

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



    public SongListViewModel() {
        //to get use
    }

    public List<Songs> getSongsList() {
        return songsList;
    }

    public void setSongsList(List<Songs> songsList) {
        this.songsList = songsList;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}
