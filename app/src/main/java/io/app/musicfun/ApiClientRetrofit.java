package io.app.musicfun;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientRetrofit {

    public static Retrofit getRetrofit() {

        //Init of retrofit method
        final String BASE_URL = "https://api.spotify.com/v1/playlists/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
