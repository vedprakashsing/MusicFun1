package io.app.musicfun;

import static android.content.ContentValues.TAG;

import static com.spotify.sdk.android.authentication.AuthenticationResponse.Type.TOKEN;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.DialogFragmentNavigatorDestinationBuilder;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import io.app.musicfun.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static final String CLIENT_ID ="64efafd977cc4c18938e487609f62ea2";
    private static final String REDIRECT_URI ="coolcheck://callback";
    private static final int REQUEST_CODE = 1337;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private";



    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate( inflater,container,false);
        View view=binding.getRoot();
        binding.spotifyLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: loggin");
                authenticateSpotify();
            }

        });

        return view;

    }
    public void authenticateSpotify() {
        Log.d(TAG, "onClick: intoauthentication");
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);
    }


}