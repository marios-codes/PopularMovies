package com.example.android.popularmovies.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "favorite_movie")
public class Movie implements Parcelable {

  private static final String BASE_POSTER_IMAGE_URL = "http://image.tmdb.org/t/p/w185";
  private static final String BASE_BACKDROP_IMAGE_URL = "http://image.tmdb.org/t/p/w500";

  @SerializedName("id")
  @Expose
  @PrimaryKey //Used By Room
  private Integer id;
  @SerializedName("vote_average")
  @Expose
  @ColumnInfo(name = "user_rating")
  private Double userRating;
  @SerializedName("poster_path")
  @Expose
  @ColumnInfo(name = "poster_path")
  private String posterPath;
  @SerializedName("backdrop_path")
  @Expose
  @ColumnInfo(name = "backdrop_path")
  private String backdropPath;
  //Used "title" instead of "original_title" to have a consistent experience in English
  @SerializedName("title")
  @Expose
  private String title;
  @SerializedName("overview")
  @Expose
  @ColumnInfo(name = "plot_synopsis")
  private String plotSynopsis;
  @SerializedName("release_date")
  @Expose
  @ColumnInfo(name = "release_date")
  private String releaseDate;

  /**
   * No args constructor for use in serialization
   *
   */
  @Ignore //Used by Room to ignore this constructor
  public Movie() {
  }

  /**
   * @param id
   * @param title
   * @param userRating
   * @param posterPath
   * @param backdropPath
   * @param plotSynopsis
   * @param releaseDate
   */
  public Movie(Integer id, String title, Double userRating, String posterPath,
      String backdropPath,
      String plotSynopsis, String releaseDate) {
    super();
    this.id = id;
    this.title = title;
    this.userRating = userRating;
    this.posterPath = posterPath;
    this.backdropPath = backdropPath;
    this.plotSynopsis = plotSynopsis;
    this.releaseDate = releaseDate;
  }

  //used when un-parceling the parcel (creating the object)
  public static final Creator<Movie> CREATOR = new Creator<Movie>() {
    @Override
    public Movie createFromParcel(Parcel in) {
      return new Movie(in);
    }

    @Override
    public Movie[] newArray(int size) {
      return new Movie[size];
    }
  };

  public Integer getId() {
    return id;
  }

  public Double getUserRating() {
    return userRating;
  }

  public String getTitle() {
    return title;
  }

  public String getPosterPath() {
    String imagePath = posterPath;
    if (!posterPath.contains(BASE_POSTER_IMAGE_URL)) {
      imagePath = BASE_POSTER_IMAGE_URL + posterPath;
    }
    return imagePath;
  }

  public String getBackdropPath() {
    String imagePath = backdropPath;
    if (!backdropPath.contains(BASE_BACKDROP_IMAGE_URL)) {
      imagePath = BASE_BACKDROP_IMAGE_URL + backdropPath;
    }
    return imagePath;
  }

  public String getPlotSynopsis() {
    return plotSynopsis;
  }

  public void setPlotSynopsis(String plotSynopsis) {
    this.plotSynopsis = plotSynopsis;
  }

  public String getReleaseDate() {
    return releaseDate;
  }

  //Parcelable Constructor
  protected Movie(Parcel in) {
    id = in.readInt();
    title = in.readString();
    userRating = in.readDouble();
    posterPath = in.readString();
    backdropPath = in.readString();
    plotSynopsis = in.readString();
    releaseDate = in.readString();
  }

  //Parcelable Implementation
  @Override
  public int describeContents() {
    return 0;
  }

  //write object values to parcel for storage
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(title);
    dest.writeDouble(userRating);
    dest.writeString(posterPath);
    dest.writeString(backdropPath);
    dest.writeString(plotSynopsis);
    dest.writeString(releaseDate);
  }
}