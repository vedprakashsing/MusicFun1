package io.app.musicfun;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.app.musicfun.databinding.FragmentMusicPlayerMinimizeBinding;


public class MusicPlayerMinimize extends Fragment {

  private FragmentMusicPlayerMinimizeBinding binding;

    public MusicPlayerMinimize() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentMusicPlayerMinimizeBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        return view;
    }
}