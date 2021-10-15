package io.app.musicfun;

import static android.content.ContentValues.TAG;
import static com.spotify.sdk.android.authentication.AuthenticationResponse.Type.TOKEN;

import static java.util.jar.Pack200.Packer.ERROR;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.protocol.types.Album;

import io.app.musicfun.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String CLIENT_ID ="64efafd977cc4c18938e487609f62ea2";
    private static final String REDIRECT_URI ="coolcheck://callback";
    private static final int REQUEST_CODE = 1337;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private";
    public SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view= binding.getRoot();
        setContentView(view);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Toast.makeText(this,"Hogya",Toast.LENGTH_SHORT).show();
                    // Handle successful response
                    ConnectionParams connectionParams =
                            new ConnectionParams.Builder(CLIENT_ID)
                                    .setRedirectUri(REDIRECT_URI)
                                    .showAuthView(true)
                                    .build();

                    SpotifyAppRemote.connect(this, connectionParams,
                            new Connector.ConnectionListener() {

                                public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                                    mSpotifyAppRemote = spotifyAppRemote;
                                    Log.d("MainActivity", "Connected! Yay!");
                                    connection();

                                }

                                public void onFailure(Throwable throwable) {
                                    Log.e("MyActivity", throwable.getMessage()+"Sex", throwable);

                                    // Something went wrong when attempting to connect! Handle errors here
                                    Toast.makeText(MainActivity.this,"Some error occur retry",Toast.LENGTH_SHORT).show();
                                }
                            });

                    break;

                // Auth flow returned an error
                case ERROR:
                    Toast.makeText(this,"naho hua",Toast.LENGTH_SHORT).show();
                    // Handle error responseimplementation 'androidx.browser:browser:1.0.0'
                    break;

                // Most likely auth flow was cancelled
                default:
                    Toast.makeText(this,"kuch hua hi nahi",Toast.LENGTH_SHORT).show();
                    // Handle other cases
            }
        }
    }

    public void connection(){


        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

}