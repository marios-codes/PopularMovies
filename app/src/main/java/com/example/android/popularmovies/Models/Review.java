package com.example.android.popularmovies.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {

  @SerializedName("author")
  @Expose
  private String author;
  @SerializedName("content")
  @Expose
  private String content;


  /**
   * No args constructor for use in serialization
   *
   */
  public Review() {
  }

  /**
   *
   * @param content the comment string
   * @param author author of the comment
   */
  public Review (String author, String content) {
    super();
    this.author = author;
    this.content = content;
  }

  public String getAuthor() {
    return author;
  }

  public String getContent() {
    return content;
  }
}
