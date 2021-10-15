package io.app.musicfun.ListAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.app.musicfun.Models.Status;
import io.app.musicfun.R;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import io.app.musicfun.databinding.FragmentHomeBinding;
import io.app.musicfun.databinding.StatusBinding;

public class StatusListAdapter extends RecyclerView.Adapter<StatusListAdapter.ViewHolder> {

    private List<Status> statusList;
    private Context context;

   public StatusListAdapter(Context context,List<Status> statuses){
       this.context=context;
       statusList=statuses;
   }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView statusImage;
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            statusImage=itemView.findViewById(R.id.status_image);
            name=(TextView)itemView.findViewById(R.id.nameStatusBelong);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new StatusListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.status,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       Status status= statusList.get(position);
       String statusUlrString=status.getStatusUrl();
        Uri statusUlr=Uri.parse(statusUlrString);
       String name=status.getUserName();

       holder.name.setText(name);
        Glide
                .with(context)
                .load(statusUlr)
                .centerCrop()
                .into(holder.statusImage);
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }


}
