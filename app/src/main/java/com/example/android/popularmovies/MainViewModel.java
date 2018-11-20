package com.example.android.popularmovies;

import static com.example.android.popularmovies.MainActivity.API_KEY;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;
import com.example.android.popularmovies.Database.AppDatabase;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Models.RetrofitResponse.MoviesResponse;
import com.example.android.popularmovies.Network.MovieDBInterface;
import com.example.android.popularmovies.Network.MovieDBUtils;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

  private static final String TAG = MainViewModel.class.getSimpleName();
  private MovieDBInterface mMovieDBInterface;
  private MutableLiveData<List<Movie>> popularMovieList;
  private MutableLiveData<List<Movie>> topRatedMovieList;

  private enum SortedBy {POPULARITY, TOP_RATED}

  public MainViewModel () {
    mMovieDBInterface = MovieDBUtils.setupMovieDbInterface();
  }


  public LiveData<List<Movie>> getFavoriteMovies(Context context) {
    AppDatabase database = AppDatabase.getInstance(context.getApplicationContext());
    LiveData<List<Movie>> favoriteMovies = database.movieDAO().loadAllFavoriteMovies();
    return favoriteMovies;
  }

  public LiveData<List<Movie>> getPopularMovies() {
    if (popularMovieList == null) {
      popularMovieList = new MutableLiveData<>();
      //load data from the MovieDB Api
      loadMovies(SortedBy.POPULARITY);
    }
    return popularMovieList;
  }

  public LiveData<List<Movie>> getTopRatedMovies() {
    if (topRatedMovieList == null) {
      topRatedMovieList = new MutableLiveData<>();
      //load data from the MovieDB Api
      loadMovies(SortedBy.TOP_RATED);
    }
    return topRatedMovieList;
  }

  private void loadMovies(final SortedBy sortedBy) {
    Call<MoviesResponse> moviesRequest;
    if (sortedBy.equals(SortedBy.POPULARITY)) {
      moviesRequest = mMovieDBInterface
          .getPopularMoviesList(API_KEY);
    } else {
      moviesRequest = mMovieDBInterface
          .getTopRatedMoviesList(API_KEY);
    }

    moviesRequest.enqueue(new Callback<MoviesResponse>() {
      @Override
      public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
        if (response.isSuccessful()) {
          MoviesResponse moviesResponse = response.body();
          if (moviesResponse != null) {
            if (sortedBy.equals(SortedBy.POPULARITY)) {
              popularMovieList.setValue(moviesResponse.getMovies());
            } else {
              topRatedMovieList.setValue(moviesResponse.getMovies());
            }
          } else {
            //server response is null
            if (sortedBy.equals(SortedBy.POPULARITY)) {
              popularMovieList.setValue(null);
            } else {
              topRatedMovieList.setValue(null);
            }
            Log.w(TAG, "onResponse: Server Response = null");
          }
        } else {
          Log.w(TAG, "onResponse: Response unsuccessful with code: " + response.code());
        }

      }

      @Override
      public void onFailure(Call<MoviesResponse> call, Throwable t) {
        popularMovieList.setValue(null);
        Log.e(TAG, "onFailure: Throwable: " + t.getMessage());
      }
    });
  }
}
