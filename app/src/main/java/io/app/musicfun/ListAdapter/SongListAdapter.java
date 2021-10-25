package io.app.musicfun.ListAdapter;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.app.musicfun.Interface.AdapterToFragment;
import io.app.musicfun.Models.Songs;
import io.app.musicfun.MusicPlayerFragment;
import io.app.musicfun.R;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

    private Context context;
    private List<Songs> songsList;
    private AdapterToFragment adapterToFragment;

    public SongListAdapter(Context context, List<Songs> songsList, AdapterToFragment adapterToFragment){
     this.context=context;
     this.songsList=songsList;
     this.adapterToFragment=adapterToFragment;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        //private final TextView name;
        private final TextView title;
        private final TextView duration;
        private final TextView artist;
        private final CardView deviceMusicCardView;
        private final ImageView thumbnail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder:DeviceFragment");
             //name=itemView.findViewById(R.id.songName);
             title=itemView.findViewById(R.id.songTitle);
             duration=itemView.findViewById(R.id.songDuration);
             artist=itemView.findViewById(R.id.songArtist);
             deviceMusicCardView=itemView.findViewById(R.id.deviceMusicCardView);
             thumbnail=itemView.findViewById(R.id.songThumbnail);

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
        int songPosition=position;
        int totalSongs=getItemCount();
        String name= songs.getName();
        String title= songs.getTitle();
        int duration=  songs.getDuration();
        String artist= songs.getArtist();
        Bitmap thumbnail=songs.getThumbnail();
        Log.d(TAG, "position"+songPosition);

        holder.thumbnail.setImageBitmap(thumbnail);
        holder.title.setText(title);
        //holder.name.setText("NAME:-"+name);
        holder.duration.setText("Duration:-"+ TimeUnit.SECONDS.toMinutes(TimeUnit.MILLISECONDS.toSeconds(duration))+"."+(TimeUnit.MILLISECONDS.toSeconds(duration)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
        holder.artist.setText("Artist:-"+artist);

        holder.deviceMusicCardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "MinimusicPlayer"+((Activity)view.getContext()).findViewById(R.id.musicMiniPlayer).getVisibility());
                if(((Activity)view.getContext()).findViewById(R.id.musicMiniPlayer).getVisibility()==View.VISIBLE){
                    ((Activity)view.getContext()).findViewById(R.id.musicMiniPlayer).setVisibility(View.GONE);
                }
                Bundle result=new Bundle();
                Log.d(TAG, "getItemCount:-"+getItemCount());
                result.putString("songUriStringKey",songs.getSongUri().toString());
                result.putInt("songPositionKey",songPosition);
                result.putInt("totalSongKey",totalSongs);
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().setFragmentResult("bundleSongKey",result);
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction().addToBackStack("deviceMusicFragment").replace(R.id.nav_host_fragment_container, MusicPlayerFragment.class,null).commit();

            }
        });
    }


    @Override
    public int getItemCount() {
        return songsList.size();
    }


}
