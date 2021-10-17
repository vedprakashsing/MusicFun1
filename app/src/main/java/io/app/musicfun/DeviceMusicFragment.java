package io.app.musicfun;

import static android.content.ContentValues.TAG;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.app.musicfun.ListAdapter.SongListAdapter;
import io.app.musicfun.Models.Songs;
import io.app.musicfun.databinding.FragmentDeviceMusicFragementBinding;


public class DeviceMusicFragment extends Fragment {

private FragmentDeviceMusicFragementBinding binding;
    public DeviceMusicFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragme nt
        List<Songs> songs = new ArrayList<Songs>();
        binding=FragmentDeviceMusicFragementBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        String[] projection=new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DISPLAY_NAME
        };


        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder=MediaStore.Audio.Media.DISPLAY_NAME+" ASC";

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
           collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

       try( Cursor cursor = this.getContext().getContentResolver().query(
                collection,
                projection,
                null,
                null,
                sortOrder
        )) {


           int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
           int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
           int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
           int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
           int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
           int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);

           while (cursor.moveToNext()) {
               long id = cursor.getLong(idColumn);
               String name = cursor.getString(nameColumn);
               int duration = cursor.getInt(durationColumn);
               int size = cursor.getInt(sizeColumn);
               String artist = cursor.getString(artistColumn);
               String title = cursor.getString(titleColumn);

               Log.d(TAG, "onCreateView: huu" + name+","+id+","+duration+","+size+","+artist+","+title);

               Uri contentUri = ContentUris.withAppendedId(
                       MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
               Songs song = new Songs(contentUri, name, artist, title, id, duration, size);
               songs.add(song);
           }
           cursor.close();

           SongListAdapter songListAdapter=new SongListAdapter(this.getContext(),songs);
           binding.songsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
           binding.songsRecyclerView.setAdapter(songListAdapter);


       }
        return view;
    }
}