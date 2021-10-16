package io.app.musicfun.ListAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.app.musicfun.Models.Songs;
import io.app.musicfun.R;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

    private Context context;
    private List<Songs> songsList;
    public SongListAdapter(Context context,List<Songs> songsList){
     this.context=context;
     this.songsList=songsList;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView title;
        private final TextView duration;
        private final TextView artist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             name=itemView.findViewById(R.id.songName);
             title=itemView.findViewById(R.id.songTitle);
             duration=itemView.findViewById(R.id.songDuration);
             artist=itemView.findViewById(R.id.songArtist);
        }
    }
    @NonNull
    @Override
    public SongListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listofsongstype, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Songs songs=songsList.get(position);
        String name= songs.getName();
        String title= songs.getTitle();
        int duration=  songs.getDuration();
        String artist= songs.getArtist();

        holder.title.setText("Title:-"+title);
        holder.name.setText("NAME:-"+name);
        holder.duration.setText("Duration:-"+duration);
        holder.artist.setText("Artist:-"+artist);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }


}
