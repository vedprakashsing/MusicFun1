package io.app.musicfun.Models;

import android.net.Uri;

public class Songs {
    private Uri songUri;
    private String name;
    private String artist;
    private String title;
    private long id;
    private int duration;
    private int size;

    public Songs(Uri songUri, String name, String artist, String title, long id, int duration, int size) {
        this.songUri = songUri;
        this.name = name;
        this.artist = artist;
        this.title = title;
        this.id = id;
        this.duration = duration;
        this.size = size;
    }

    public Uri getSongUri() {
        return songUri;
    }

    public void setSongUri(Uri songUri) {
        this.songUri = songUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
