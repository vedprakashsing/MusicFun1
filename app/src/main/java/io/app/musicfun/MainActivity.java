package io.app.musicfun;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.content.res.Configuration;

import com.google.android.material.navigation.NavigationBarView;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.xmlpull.v1.XmlPullParser;

import io.app.musicfun.ViewModel.SongListViewModel;
import io.app.musicfun.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private ActivityMainBinding binding;
    private static final String CLIENT_ID = "64efafd977cc4c18938e487609f62ea2";
    private static final String REDIRECT_URI = "coolcheck://callback";
    private static final int REQUEST_CODE = 1;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private";
    public SpotifyAppRemote mSpotifyAppRemote;
    private boolean checkPermission;
    int nexItem;
    int [][]states;
    int[]color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        bottomNavigation(binding.bottomToolbar);
        final SongListViewModel songListViewModel = new ViewModelProvider(this).get(SongListViewModel.class);
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
                    Toast.makeText(this, "Hogya", Toast.LENGTH_SHORT).show();
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
                                    Log.e("MyActivity", throwable.getMessage() + "Sex", throwable);

                                    // Something went wrong when attempting to connect! Handle errors here
                                    Toast.makeText(MainActivity.this, "Some error occur retry", Toast.LENGTH_SHORT).show();
                                }
                            });

                    break;

                // Auth flow returned an error
                case ERROR:
                    Toast.makeText(this, "naho hua", Toast.LENGTH_SHORT).show();
                    // Handle error responseimplementation 'androidx.browser:browser:1.0.0'
                    break;

                // Most likely auth flow was cancelled
                default:
                    Toast.makeText(this, "kuch hua hi nahi", Toast.LENGTH_SHORT).show();
                    // Handle other cases
            }
        }
    }

    public void connection() {


        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });
    }

//Start of bottomNavigation
    public void bottomNavigation(View view) {
        binding.bottomToolbar.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        final int previousItem = binding.bottomToolbar.getSelectedItemId();
                        final int nextItem = item.getItemId();
                        nexItem = nextItem;
                        if (previousItem != nextItem) {

                            if (nextItem == R.id.bottom_bar_home) {
                                Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                //transaction.addToBackStack("deviceMusicFragment");
                                transaction.replace(R.id.nav_host_fragment_container, HomeFragment.class, null);
                                // Commit the transaction
                                transaction.commit();
                                return true;
                            } else if (nextItem == R.id.bottom_bar_library) {
                                if (checkPermission) {
                                    Toast.makeText(MainActivity.this, "Library", Toast.LENGTH_SHORT).show();
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                                    //transaction.setReorderingAllowed(true);
                                    //transaction.addToBackStack("deviceMusicFragment");
                                    transaction.replace(R.id.nav_host_fragment_container, DeviceMusicFragment.class, null);
                                    // Commit the transaction
                                    transaction.commit();
                                } else {
                                    runTimePermission();
                                }
                                return true;
                            } else {
                                if(getSupportFragmentManager().getBackStackEntryCount()>0){
                                    getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(),FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_container,SettingFragment.class,null).commit();
                                }else{
                                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_container,SettingFragment.class,null).commit();
                                }

                                Toast.makeText(MainActivity.this, "Setting", Toast.LENGTH_SHORT).show();
                                return true;
                            }

                        } else {

                            if (nextItem == R.id.bottom_bar_library) {
                                Log.d("check", "onNavigationItemSelected: "+checkPreOfMediaPlayerFrag());
                                if (checkPermission) {
                                    if(checkPreOfMediaPlayerFrag()) {
                                        getSupportFragmentManager().popBackStack();
                                    }

                                }else {
                                    runTimePermission();
                                }

                            }
                        }
                        return true;
                    }
                }
        );
        //Reacting according to ItemId
    }
    //END


    //Start of onConfigurationChange
   public void onConfigurationChanged (Configuration newConfig){
        super.onConfigurationChanged(newConfig);

        int currentConfigMode= newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentConfigMode){
            case Configuration.UI_MODE_NIGHT_NO:
                binding.bottomToolbar.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.bottom_navigationbar_background));
                ColorStateList colorStateList=getResources().getColorStateList(R.color.item_text_color,null);
                binding.bottomToolbar.setItemTextColor(colorStateList);

                Log.d("tag", "onConfigurationChanged: "+colorStateList);
                break;
            case Configuration.UI_MODE_NIGHT_YES:

                 binding.bottomToolbar.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.bottom_navigationbar_background_night));
                 colorStateList=getColorStateList(R.color.item_text_color_night);
                 binding.bottomToolbar.setItemTextColor(colorStateList);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentConfigMode);
        }

    }


    @Override
    protected void onNightModeChanged(int mode) {
        super.onNightModeChanged(mode);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("check", "onStart: Started");
        runTimePermission();

    }


    public void runTimePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            // Toast.makeText(MainActivity.this,"Thanks",Toast.LENGTH_SHORT).show();
            checkPermission = true;
        } else {
            // You can directly ask for the permission.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }
    }


    public void quitApp() {
        MainActivity.this.finish();
        //MainActivity.this.finishAndRemoveTask();(This is use for also release from front)
        System.exit(1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission = true;
                    if (nexItem == R.id.bottom_bar_library) {
                        Toast.makeText(MainActivity.this, "Library", Toast.LENGTH_SHORT).show();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        //transaction.setReorderingAllowed(true);
                        transaction.addToBackStack("true");
                        transaction.replace(R.id.nav_host_fragment_container, DeviceMusicFragment.class, null);
                        // Commit the transaction
                        transaction.commit();
                    }

                } else {

                    checkPermission = false;
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


     public boolean checkPreOfMediaPlayerFrag(){
        try {
            MusicPlayerFragment myFragment = (MusicPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
            if (myFragment != null && myFragment.isVisible()) {
                //getSupportFragmentManager().popBackStack();
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
         }
     }

     @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(MainActivity.this,"ActivityGetDestroed",Toast.LENGTH_SHORT).show();
     }


}