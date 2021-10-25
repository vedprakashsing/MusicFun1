package io.app.musicfun;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.ViewModelProvider;
import io.app.musicfun.Interface.AdapterToFragment;
import io.app.musicfun.ListAdapter.SongListAdapter;
import io.app.musicfun.Models.Songs;
import io.app.musicfun.ViewModel.SongListViewModel;
import io.app.musicfun.databinding.FragmentDeviceMusicFragementBinding;


public class DeviceMusicFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =123;
    private FragmentDeviceMusicFragementBinding binding;
    private SongListViewModel songListViewModel;


    public DeviceMusicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart(){
        super.onStart();
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true){

            @Override
            public void handleOnBackPressed(){

            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        List<File> song = new ArrayList<>();
        binding = FragmentDeviceMusicFragementBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        List<Songs> songs = new ArrayList<>();
        songListViewModel=new ViewModelProvider(getActivity()).get(SongListViewModel.class);
        AdapterToFragment adapterToFragment=new AdapterToFragment() {
            @Override
            public void pause( MediaPlayer mediaPlayer) {
                mediaPlayer.pause();
            }
            @Override
            public void start(MediaPlayer mediaPlayer){
                mediaPlayer.start();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            songs=getSongs();
        }
        SongListAdapter songListAdapter=new SongListAdapter(this.getContext(),songs,adapterToFragment);
        binding.songsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.songsRecyclerView.setAdapter(songListAdapter);

        return view;
    }

   //Get the list of Songs
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public List<Songs> getSongs() {

        List<Songs> songs = new ArrayList<Songs>();
        String[] projection = new String[]{
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                //MediaStore.Audio.AudioColumns.RELATIVE_PATH
        };


        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            Log.d(TAG, "onCreateView:Recheck" + collection);
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Log.d(TAG, "onCreateView:Recheck" + collection);
        }

        try (Cursor cursor = this.getContext().getContentResolver().query(
                collection,
                projection,
                null,
                null,
                sortOrder
        )) {


            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST);
            //int songsUri=cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.RELATIVE_PATH);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);
                String artist = cursor.getString(artistColumn);
                String title = cursor.getString(titleColumn);
                //String songUri=cursor.getString(songsUri);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                Bitmap thumbnail=null;
                try {
                    thumbnail = getContext().getContentResolver().loadThumbnail(contentUri, new Size(600,600), null);
                }catch (Exception e){
                     thumbnail= BitmapFactory.decodeResource(getResources(),R.drawable.music_logo);
                }

                Log.d(TAG, "onCreateView: huu" + name);


                // Log.d(TAG, "onCreateView: huu" + name+"check:-"+songUri+"Check:-"+contentUri);
                Songs song = new Songs(contentUri, name, artist, title, id, duration, size,thumbnail);
                songs.add(song);
            }
            cursor.close();
        }
        songListViewModel.setSongsList(songs);
        return songs;
    }


}
