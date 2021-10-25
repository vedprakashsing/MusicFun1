package io.app.musicfun;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.app.musicfun.Models.Songs;
import io.app.musicfun.ViewModel.SongListViewModel;
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
    private int position;
    private int totalSong;
    private String currentSongName;
    private SongListViewModel songListViewModel;
    private List<Songs> songsList;
    private boolean shuffleSet;


    public MusicPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        //Overriding BackPressed Action
        OnBackPressedCallback callBack=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStack();
                getActivity().findViewById(R.id.musicMiniPlayer).setVisibility(View.VISIBLE);
            }
        };
     getActivity().getOnBackPressedDispatcher().addCallback(this,callBack);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mContext = this.getContext();
        songListViewModel = new ViewModelProvider(getActivity()).get(SongListViewModel.class);

        //Getting Data From SongListAdapter
        getParentFragmentManager().setFragmentResultListener("bundleSongKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                result = bundle.getString("songUriStringKey");
                position = bundle.getInt("songPositionKey");
                Log.d(TAG, "playNext "+position);
                totalSong = bundle.getInt("totalSongKey");
                songsList = songListViewModel.getSongsList();
                Log.d(TAG, "songPosition:-" + position + totalSong + songsList.size());
                songUri = Uri.parse(result);

                //Check loop and shuffle'
                if(songListViewModel.isLoopingSong()) {
                    Log.d(TAG, "songListView"+songListViewModel.isLoopingSong());
                    if (mediaPlayer.isLooping()) {
                        binding.loopOption.setBackground(getActivity().getDrawable(R.drawable.selected_menu_in_player_background));
                        setMediaPlayer();
                    }else{
                        binding.loopOption.setBackground(getActivity().getDrawable(R.drawable.selected_menu_in_player_background));
                        setMediaPlayer();
                        mediaPlayer.setLooping(true);
                    }
                }
                //END

                //Check Shuffle
                else if(songListViewModel.isShuffleSong()) {
                    if (shuffleSet) {
                        binding.shuffleOption.setBackground(getActivity().getDrawable(R.drawable.selected_menu_in_player_background));
                        setMediaPlayer();
                    }else{
                        shuffleSet=true;
                        binding.shuffleOption.setBackground(getActivity().getDrawable(R.drawable.selected_menu_in_player_background));
                        setMediaPlayer();
                    }
                }
                //END

               else {
                    setMediaPlayer();
                }
            }
        });
        //END


        //Setting onCLickListener to remove some view
          binding.musicPlayerDownButton.setOnClickListener(new View.OnClickListener(){

              @Override
              public void onClick(View view){

                  songListViewModel.setMediaPlayer(mediaPlayer);
                  getParentFragmentManager().popBackStack();
                  getActivity().findViewById(R.id.musicMiniPlayer).setVisibility(View.VISIBLE);
              }
          });
        //END

        //Setting onCLickListener to play prev song
        binding.previousPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPrevSong();
            }
        });
        //END

        //Setting onClickListener to play net song
        binding.nextPlayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                playNextSong();
            }
        });
        //END



        //Setting onClickListener to set Loop
        binding.loopOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!songListViewModel.isLoopingSong()){
                    Toast.makeText(getContext(),"Loop Got ON",Toast.LENGTH_SHORT).show();
                    mediaPlayer.setLooping(true);
                    songListViewModel.setLoopingSong(true);
                    binding.loopOption.setBackground(getActivity().getDrawable(R.drawable.selected_menu_in_player_background));
                }else {
                    Toast.makeText(getContext(),"Loop Got OFF",Toast.LENGTH_SHORT).show();
                    mediaPlayer.setLooping(false);
                    songListViewModel.setLoopingSong(false);
                    binding.loopOption.setBackground(getActivity().getDrawable(R.drawable.non_selected_menu_player_background));
                }
            }
        });
        //END


        //Setting onClickListenr to set randomSong
        binding.shuffleOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!songListViewModel.isShuffleSong()){
                    Toast.makeText(getContext(),"Shuffle Got ON",Toast.LENGTH_SHORT).show();
                    shuffleSet=true;
                    songListViewModel.setShuffleSong(true);
                    binding.shuffleOption.setBackground(getActivity().getDrawable(R.drawable.selected_menu_in_player_background));
                }else{
                    Toast.makeText(getContext(),"Shuffle Got OFF",Toast.LENGTH_SHORT).show();
                    shuffleSet=false;
                    songListViewModel.setShuffleSong(false);
                    binding.shuffleOption.setBackground(getActivity().getDrawable(R.drawable.non_selected_menu_player_background));
                }

            }
        });
        //END

        //Setting onClickListener to play and pause
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
                } else {
                    if (grabAudioFocus()) {
                        mediaPlayer.start();
                        Glide
                                .with(mContext)
                                .load(R.drawable.pause)
                                .centerCrop()
                                .into(binding.musicPlayerPlayButton);
                        createTimerSongs();
                    }
                }

            }
        });
        //END

        //Setting onCompletionListener to check whether the song get completed or not
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                binding.musicProgressBar.setProgress(mediaPlayer.getDuration());
                Log.d(TAG, " playNextonCompletion: "+position);
                Glide
                        .with(mContext)
                        .load(R.drawable.play)
                        .centerCrop()
                        .into(binding.musicPlayerPlayButton);

                if(mediaPlayer.isLooping()){
                    mediaPlayer.start();
                }else if(shuffleSet){
                        Random positionRandom = new Random();
                        position = positionRandom.nextInt(songsList.size() - 1);
                        playNextSong();
                }else {
                    playNextSong();
                }
            }
        });
        //END

        //_audioFocusChangeListener initialize here
        _audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_GAIN");
                        mediaPlayer.start();
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS:
                        // Lost focus for an unbounded amount of time: stop playback and
                        // release media player
                        Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS");
                        mediaPlayer.pause();
                        final Activity activity = (Activity) mContext;
                        if (!activity.isDestroyed()) {
                            Glide
                                    .with(mContext)
                                    .load(R.drawable.play)
                                    .centerCrop()
                                    .into(binding.musicPlayerPlayButton);
                        }
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
                    default:
                        throw new IllegalStateException("Unexpected value: " + focusChange);
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


//Start of Creating Timer
    public void createTimerSongs() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (mediaPlayer != null || mediaPlayer.isPlaying()) {
                    binding.updateTimeOfSong.post(new Runnable() {
                        @Override
                        public void run() {
                            final int[] currentDuration = {mediaPlayer.getCurrentPosition()};
                            //String.format("%d min", TimeUnit.MILLISECONDS.toMinutes(currentDuration));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                binding.musicProgressBar.setMin(0);
                            }
                            totalDuration = mediaPlayer.getDuration();
                            binding.musicProgressBar.setMax(mediaPlayer.getDuration());
                            binding.updateTimeOfSong.setText(timeToMinutes(currentDuration[0]) + "." + timeToSeconds(currentDuration[0]));
                            binding.remainingTimeOfSong.setText(remaningTime(totalDuration, currentDuration[0]));
                            binding.musicProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                    if (b) {
                                        currentDuration[0] = i;
                                        mediaPlayer.seekTo(i);
                                        binding.updateTimeOfSong.setText(timeToMinutes(currentDuration[0]) + "." + timeToSeconds(currentDuration[0]));
                                        binding.remainingTimeOfSong.setText(remaningTime(totalDuration, currentDuration[0]));
                                        Log.d(TAG, "onProgressChanged: Change");
                                    }

                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {
                                    binding.musicProgressBar.computeScroll();
                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                    // mediaPlayer.seekTo(seekBar.getProgress());
                                    //binding.updateTimeOfSong.setText(String.format("%2d.%2d", TimeUnit.MILLISECONDS.toMinutes(currentDuration),TimeUnit.MILLISECONDS.toSeconds(currentDuration)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentDuration))));
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
    //End

    //Start of timeToMinutes
    public String timeToMinutes(int duration) {
        String minuteString = "";
        duration = (int) TimeUnit.MILLISECONDS.toMinutes(duration);
        if (duration < 10) {
            minuteString = "0" + duration;
            return minuteString;
        } else {
            minuteString = duration + "";
            return minuteString;
        }
    }
    //End

    //Start fo timeToSeconds
    public String timeToSeconds(int duration) {
        String secondString = "";
        duration = (int) (TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        if (duration < 10) {
            secondString = "0" + duration;
            return secondString;
        } else {
            secondString = duration + "";
            return secondString;
        }
    }
   //END

    //Start of remaingTime
    public String remaningTime(int totalDuration, int currentDuration) {
        String remainTime = "";
        int currentTimeLong = totalDuration - currentDuration;
        remainTime = timeToMinutes(currentTimeLong) + "." + timeToSeconds(currentTimeLong);
        return remainTime;
    }
    //END

    //Start of playPrevSong
    public void playPrevSong() {

        if(position<=0){
            position=songsList.size();
        }
        if (position >= 1) {
            position--;
            Songs song = songsList.get(position);
            songUri = song.getSongUri();
             setMediaPlayer();
        }
    }
   //End

    //Start of playNextSong
    public void playNextSong(){
          if(position>=songsList.size()-1){
              position=0;
              Songs song=songsList.get(position);
              songUri=song.getSongUri();
              setMediaPlayer();
              Log.d(TAG, "playNextSong: "+songsList.size());
          }else if(position<songsList.size()-1 && position>=0){
            position++;
            Log.d(TAG, "playNextSong:postion "+position);
            Songs song=songsList.get(position);
            songUri=song.getSongUri();
            setMediaPlayer();
        }
    }
    //END

    //Start of setMediaPlayer
    public void setMediaPlayer() {

        if (grabAudioFocus()) {

            Log.d(TAG, "onAudioFocusChange: " + grabAudioFocus());
            mediaPlayer.reset();//Causing no effect when create first time.
            binding.musicThumbnail.setImageBitmap(songsList.get(position).getThumbnail());
            binding.currentSongName.setText(songsList.get(position).getTitle());
            binding.currentSongName.setSelected(true);
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            try {
                mediaPlayer.setDataSource(mContext, songUri);
                mediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
                mediaPlayer.prepare();
                Log.d(TAG, "onClick: Start");
                mediaPlayer.start();
                Glide
                        .with(mContext)
                        .load(R.drawable.pause)
                        .centerCrop()
                        .into(binding.musicPlayerPlayButton);
                createTimerSongs();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mContext, "Check", Toast.LENGTH_SHORT).show();
        }

    }
    //END




}