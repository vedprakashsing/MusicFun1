package io.app.musicfun.ListAdapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.app.musicfun.Models.Songs;
import io.app.musicfun.MusicPlayerFragment;
import io.app.musicfun.R;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

    private Context context;
    private List<Songs> songsList;
    private AudioManager.OnAudioFocusChangeListener _audioFocusChangeListener;
    private Context mContext;
    private Uri songsUri;
    private MediaPlayer mediaPlayer=new MediaPlayer();

    public SongListAdapter(Context context,List<Songs> songsList){
     this.context=context;
     this.songsList=songsList;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView title;
        private final TextView duration;
        private final TextView artist;
        private final CardView deviceMusicCardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder:DeviceFragment");
             name=itemView.findViewById(R.id.songName);
             title=itemView.findViewById(R.id.songTitle);
             duration=itemView.findViewById(R.id.songDuration);
             artist=itemView.findViewById(R.id.songArtist);
             deviceMusicCardView=itemView.findViewById(R.id.deviceMusicCardView);
        }
    }
    @NonNull
    @Override
    public SongListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listofsongstype, parent, false);
        Log.d(TAG, "onCreateViewHolder: DeviceFragment");

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Songs songs=songsList.get(position);
        String name= songs.getName();
        String title= songs.getTitle();
        int duration=  songs.getDuration();
        String artist= songs.getArtist();
        Log.d(TAG, "DeviceFragment" + name+","+","+duration+","+","+artist+","+title);

        holder.title.setText("Title:-"+title);
        holder.name.setText("NAME:-"+name);
        holder.duration.setText("Duration:-"+duration);
        holder.artist.setText("Artist:-"+artist);

        holder.deviceMusicCardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mContext= view.getContext();
               // Toast.makeText(view.getContext(), "Hello",Toast.LENGTH_SHORT).show();
                ((FragmentActivity)view.getContext()).getSupportFragmentManager().beginTransaction().addToBackStack("true").replace(R.id.nav_host_fragment_container,MusicPlayerFragment.class,null).commit();
                if(grabAudioFocus()){
                    //Log.d(TAG, "onAudioFocusChange: "+grabAudioFocus());
                    mediaPlayer.reset();
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    );
                    try {
                        mediaPlayer.setDataSource(view.getContext(), songs.getSongUri());
                        mediaPlayer.setWakeMode(view.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
                        mediaPlayer.prepare();
                        Log.d(TAG, "onClick: Start");
                        mediaPlayer.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(view.getContext(), "Check",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //_audioFocusChangeListener initialize here
                 _audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                    public void onAudioFocusChange(int focusChange) {
                        switch (focusChange) {
                            case AudioManager.AUDIOFOCUS_GAIN:
                                // resume playback in case of transient loss
                                try {
                                    Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_GAIN");
                                    mediaPlayer.setDataSource(mContext, songs.getSongUri());
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.start();
                                break;

                            case AudioManager.AUDIOFOCUS_LOSS:
                                // Lost focus for an unbounded amount of time: stop playback and
                                // release media player
                                Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS");
                                mediaPlayer.stop();
                                releaseAudioFocus();
                                break;

                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                // Lost focus for a short time, but we have to stop playback.
                                // We don't release the media player because playback
                                // is likely to resume
                                Log.d(TAG, "onAudioFocusChange: LOSS_TRANSIENT");
                                mediaPlayer.pause();
                                break;

                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                // Lost focus for a short time e.g device notification, but it's ok to keep playing at
                                // an attenuated level, should change volume of the player here
                                mediaPlayer.setVolume(4,4);
                                break;

                            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                                // resume playback after short playback stop
                                try {
                                    mediaPlayer.setDataSource(mContext, songs.getSongUri());
                                    mediaPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.start();

                                break;
                            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                                break;
                        }
                    }
                };


    }

    private boolean grabAudioFocus() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int result;
        Log.d(TAG, "grabAudioFocus: We are in");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = audioManager.requestAudioFocus(
                    new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setAudioAttributes(
                                    new AudioAttributes.Builder()
                                            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                                            .build())
                            .setOnAudioFocusChangeListener(_audioFocusChangeListener)
                            .build());
        } else {
            result = audioManager.requestAudioFocus(_audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

        if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            // Show Error Toast on Player Screen (if it exists)
             Toast.makeText(mContext,"Not able to play",Toast.LENGTH_SHORT).show();
            return false;
        }

        // grab the media button when we have audio focus
        return true;
    }

    public void releaseAudioFocus() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(_audioFocusChangeListener);
    }


    @Override
    public int getItemCount() {
        return songsList.size();
    }



}
