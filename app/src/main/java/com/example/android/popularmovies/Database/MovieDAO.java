package com.example.android.popularmovies.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.example.android.popularmovies.Models.Movie;
import java.util.List;

@Dao
public interface MovieDAO {

  @Query("SELECT * FROM favorite_movie")
  LiveData<List<Movie>> loadAllFavoriteMovies();

  @Query("SELECT * FROM favorite_movie WHERE id LIKE :movieID")
  LiveData<Movie> findMovieWithID(Integer movieID);

  @Insert
  void insertFavoriteMovie(Movie movie);

  @Update(onConflict = OnConflictStrategy.REPLACE)
  void updateFavoriteMovie(Movie movie);

  @Delete
  void deleteFavoriteMovie(Movie movie);
}
