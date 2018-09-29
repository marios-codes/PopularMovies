package com.example.android.popularmovies.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ServerResponse {

  @SerializedName("results")
  @Expose
  private List<Movie> movieList = null;

  /**
   * No args constructor for use in serialization
   *
   */
  public ServerResponse() {
  }

  /**
   *
   * @param movieList
   */
  public ServerResponse(List<Movie> movieList) {
    this.movieList = movieList;
  }

  public List<Movie> getMovies() {
    return movieList;
  }

  public void setMovies(List<Movie> movieList) {
    this.movieList = movieList;
  }

}
