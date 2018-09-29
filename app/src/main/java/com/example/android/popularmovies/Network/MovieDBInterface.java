package com.example.android.popularmovies.Network;

import com.example.android.popularmovies.Models.ServerResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieDBInterface {

  @GET("movie/popular")
  Call<ServerResponse> getPopularMoviesList(@Query("api_key") String apiKey);

  @GET("movie/top_rated")
  Call<ServerResponse> getTopRatedMoviesList(@Query("api_key") String apiKey);
}
