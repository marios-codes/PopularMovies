package com.example.android.popularmovies.Network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDBUtils {

  public static MovieDBInterface setupMovieDbInterface() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit.create(MovieDBInterface.class);
  }

}
