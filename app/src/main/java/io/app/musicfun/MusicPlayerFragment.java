package io.app.musicfun;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;

import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.app.musicfun.ListAdapter.SongListAdapter;
import io.app.musicfun.databinding.FragmentHomeBinding;
import io.app.musicfun.databinding.FragmentMusicPlayerBinding;


public class MusicPlayerFragment extends Fragment {

    private FragmentMusicPlayerBinding binding;
    private String result;
    private Uri songUri;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Context mContext;
    private AudioManager.OnAudioFocusChangeListener _audioFocusChangeListener;
    private int totalDuration;
    private Timer timer;

    public MusicPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mContext = view.getContext();
        getParentFragmentManager().setFragmentResultListener("bundleSongUriStringKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                result = bundle.getString("songUriStringKey");
                songUri = Uri.parse(result);

                // Do something with the result

                if (grabAudioFocus()) {
                    Log.d(TAG, "onAudioFocusChange: " + grabAudioFocus());
                    mediaPlayer.reset();//Causing no effect when create first time.
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    );
                    try {
                        mediaPlayer.setDataSource(view.getContext(), songUri);
                        mediaPlayer.setWakeMode(view.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
                        mediaPlayer.prepare();
                        Log.d(TAG, "onClick: Start");
                        mediaPlayer.start();
                       // Log.d(TAG, "onFragmentResult: "+mediaPlayer.getDuration()+mediaPlayer.getCurrentPosition());
                        createTimerSongs();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(view.getContext(), "Check", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.musicPlayerPlayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
              //  Toast.makeText(view.getContext(), result, Toast.LENGTH_SHORT).show();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    Glide
                            .with(mContext)
                            .load(R.drawable.play)
                            .centerCrop()
                            .into(binding.musicPlayerPlayButton);
                             releaseAudioFocus();
                   /* while(mediaPlayer.getCurrentPosition()!=totalDuration){

                        binding.updateTimeOfSong.setText("Please");
                    }*/
                } else {
                    if(grabAudioFocus()) {
                        mediaPlayer.start();
                        Glide
                                .with(mContext)
                                .load(R.drawable.pause)
                                .centerCrop()
                                .into(binding.musicPlayerPlayButton);
                        createTimerSongs();
                    }
                   /* while(mediaPlayer.getCurrentPosition()!=totalDuration){

                        binding.updateTimeOfSong.setText("Check");
                    }*/
                }

            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                binding.musicProgressBar.setProgress(mediaPlayer.getDuration());
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(mContext, songUri);
                    mediaPlayer.prepare();
                }catch(IOException e){
                    e.printStackTrace();
                }
                Glide
                        .with(mContext)
                        .load(R.drawable.play)
                        .centerCrop()
                        .into(binding.musicPlayerPlayButton);
            }
        });

        //_audioFocusChangeListener initialize here
        _audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        // resume playback in case of transient loss'
                   // if(grabAudioFocus()){
                        //try {
                            Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_GAIN");
                            //mediaPlayer.setDataSource(mContext, songUri);
                          //  mediaPlayer.prepare();

                            mediaPlayer.start();

                       // } catch (IOException e) {
                          //  e.printStackTrace();
                        //}
                    //}
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS:
                        // Lost focus for an unbounded amount of time: stop playback and
                        // release media player
                        Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS");
                        mediaPlayer.pause();

                        Glide
                                .with(mContext)
                                .load(R.drawable.play)
                                .centerCrop()
                                .into(binding.musicPlayerPlayButton);
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
                        mediaPlayer.setVolume(4, 4);
                        break;

                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        // resume playback after short playback stop
                        Log.d(TAG, "onAudioFocusChange: GAIN_TRANSIENT");
                        try {
                            mediaPlayer.setDataSource(mContext, songUri);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                        break;
                }
            }
        };
        //End of Implementation of AudioFocusChangeListener Interface.

        return view;
    }

    //Start of grabAudioFocus
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
            Toast.makeText(mContext, "Not able to play", Toast.LENGTH_SHORT).show();
            return false;
        }

        // grab the media button when we have audio focus
        return true;
    }
    //End of GrabAudioFocus


    public void releaseAudioFocus() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(_audioFocusChangeListener);
    }

    public void createTimerSongs(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if ( mediaPlayer.isPlaying()) {
                    binding.updateTimeOfSong.post(new Runnable() {
                        @Override
                        public void run() {
                            int currentDuration=mediaPlayer.getCurrentPosition();
                            //String.format("%d min", TimeUnit.MILLISECONDS.toMinutes(currentDuration));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                binding.musicProgressBar.setMin(0);
                            }
                            binding.musicProgressBar.setMax(mediaPlayer.getDuration());
                            binding.updateTimeOfSong.setText(String.format("%d.%d min", TimeUnit.MILLISECONDS.toMinutes(currentDuration),TimeUnit.MILLISECONDS.toSeconds(currentDuration)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentDuration))));
                            binding.musicProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                         if(b){
                                             mediaPlayer.seekTo(i);
                                         }
                                    //binding.musicProgressBar.setProgress(i);
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {


                                }
                            });
                            binding.musicProgressBar.setProgress(mediaPlayer.getCurrentPosition());
                        }
                    });
                } else {
                    timer.cancel();
                    timer.purge();
                }
            }

        }, 0, 1000);
    }
}