package com.example.android.popularmovies.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trailer {

  private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

  @SerializedName("key")
  @Expose
  private String key;
  @SerializedName("site")
  @Expose
  private String site;
  @SerializedName("type")
  @Expose
  private String type;

  /**
   * No args constructor for use in serialization
   *
   */
  public Trailer() {
  }

  /**
   *
   * @param site The site where the trailer video is hosted (They are hosted in YouTube)
   * @param type The type of video. Some examples are "Trailer", "Teaser" and "Featurette"
   * @param key The YouTube key to append on its base url to form the complete video url
   */
  public Trailer(String key, String site, String type) {
    super();
    this.key = key;
    this.site = site;
    this.type = type;
  }


  public String getUrl() {
    return YOUTUBE_BASE_URL + key;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
