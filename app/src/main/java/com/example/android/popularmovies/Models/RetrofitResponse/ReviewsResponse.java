package com.example.android.popularmovies.Models.RetrofitResponse;

import com.example.android.popularmovies.Models.Review;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReviewsResponse {

  @SerializedName("results")
  @Expose
  private List<Review> reviewList = null;

  /**
   * No args constructor for use in serialization
   *
   */
  public ReviewsResponse() {
  }

  public ReviewsResponse(List<Review> reviewList) {
    this.reviewList = reviewList;
  }

  public List<Review> getReviewList() {
    return reviewList;
  }
}
