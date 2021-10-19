package io.app.musicfun.Interface;

import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

public interface AdapterToFragment {
    void pause( MediaPlayer mediaPlayer);
    void start(MediaPlayer mediaPlayer);
}
