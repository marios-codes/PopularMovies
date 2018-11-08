package com.example.android.popularmovies.Models.RetrofitResponse;

import com.example.android.popularmovies.Models.Movie;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MoviesResponse {

  @SerializedName("results")
  @Expose
  private List<Movie> movieList = null;

  /**
   * No args constructor for use in serialization
   *
   */
  public MoviesResponse() {
  }

  public MoviesResponse(List<Movie> movieList) {
    this.movieList = movieList;
  }

  public List<Movie> getMovies() {
    return movieList;
  }

  public void setMovies(List<Movie> movieList) {
    this.movieList = movieList;
  }

}
