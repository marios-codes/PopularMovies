package com.example.android.popularmovies.Models.RetrofitResponse;

import com.example.android.popularmovies.Models.Trailer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TrailersResponse {

  @SerializedName("results")
  @Expose
  private List<Trailer> trailerList = null;

  /**
   * No args constructor for use in serialization
   *
   */
  public TrailersResponse() {
  }

  public TrailersResponse(List<Trailer> trailerList) {
    this.trailerList = trailerList;
  }

  public List<Trailer> getTrailerList() {
    return trailerList;
  }
}
