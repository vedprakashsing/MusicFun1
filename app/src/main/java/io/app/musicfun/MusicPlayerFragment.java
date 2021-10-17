package io.app.musicfun;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.app.musicfun.databinding.FragmentHomeBinding;
import io.app.musicfun.databinding.FragmentMusicPlayerBinding;


public class MusicPlayerFragment extends Fragment {

    private FragmentMusicPlayerBinding binding;

    public MusicPlayerFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentMusicPlayerBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        return view;
    }
}