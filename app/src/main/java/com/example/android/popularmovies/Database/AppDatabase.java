package com.example.android.popularmovies.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import com.example.android.popularmovies.Models.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

  private static final String TAG = AppDatabase.class.getSimpleName();
  private static final Object LOCK = new Object();
  private static final String DATABASE_NAME = "popularmovies";
  private static AppDatabase sInstance;

  public static AppDatabase getInstance(Context context) {
    if (sInstance == null) {
      synchronized (LOCK) {
        sInstance = Room
            .databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
            .build();
      }
    }
    return sInstance;
  }

  public abstract MovieDAO movieDAO();
}
