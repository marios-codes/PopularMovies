package com.example.android.popularmovies.Network;

import com.example.android.popularmovies.Models.RetrofitResponse.MoviesResponse;
import com.example.android.popularmovies.Models.RetrofitResponse.ReviewsResponse;
import com.example.android.popularmovies.Models.RetrofitResponse.TrailersResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDBInterface {

  @GET("movie/popular")
  Call<MoviesResponse> getPopularMoviesList(@Query("api_key") String apiKey);

  @GET("movie/top_rated")
  Call<MoviesResponse> getTopRatedMoviesList(@Query("api_key") String apiKey);

  @GET("movie/{movieId}/videos")
  Call<TrailersResponse> getMovieTrailers(@Path("movieId") Integer movieId,
      @Query("api_key") String apiKey);

  @GET("movie/{movieId}/reviews")
  Call<ReviewsResponse> getMovieReviews(@Path("movieId") Integer movieId,
      @Query("api_key") String apiKey);
}
