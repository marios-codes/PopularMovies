package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Models.ServerResponse;
import com.example.android.popularmovies.MoviesAdapter.MoviesAdapterOnClickHandler;
import com.example.android.popularmovies.Network.InternetCheck;
import com.example.android.popularmovies.Network.InternetCheck.Consumer;
import com.example.android.popularmovies.Network.MovieDBInterface;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MoviesAdapterOnClickHandler {

  private static final String TAG = MainActivity.class.getSimpleName();

  private static final int GRID_SPAN_COUNT = 2;

  private enum SortedBy {POPULARITY, TOP_RATED}

  private static final String API_KEY = BuildConfig.ApiKey;

  private RecyclerView mRecyclerView;
  private MoviesAdapter mMoviesAdapter;
  private ProgressBar mLoadingIndicator;
  private MovieDBInterface mDataBaseInterface;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //initialize loading indicator to avoid NPE in case there is no internet connection and
    //a sorting option from the menu is chosen
    mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

    //Check if there is Internet connectivity
    new InternetCheck(new Consumer() {
      @Override
      public void onConnectivityCheck(Boolean isConnected) {
        if (!isConnected) {
          Toast.makeText(MainActivity.this, R.string.error_no_internet,
              Toast.LENGTH_LONG).show();
        } else {
          //setup Retrofit
          mDataBaseInterface = setupMovieDbInterface();
          initViews();
        }
      }
    });
  }

  private void initViews() {
    mRecyclerView = findViewById(R.id.recyclerview_movies);
    GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, GRID_SPAN_COUNT);
    mRecyclerView.setLayoutManager(layoutManager);
    mRecyclerView.setHasFixedSize(true);

    mMoviesAdapter = new MoviesAdapter(this);
    mRecyclerView.setAdapter(mMoviesAdapter);

    loadMoviesData(mDataBaseInterface, SortedBy.POPULARITY);
  }

  private MovieDBInterface setupMovieDbInterface() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit.create(MovieDBInterface.class);
  }

  private void loadMoviesData(MovieDBInterface movieDBInterface, SortedBy sortedBy) {
    mLoadingIndicator.setVisibility(View.VISIBLE);

    Call<ServerResponse> moviesRequest;
    switch (sortedBy) {
      case POPULARITY:
        moviesRequest = movieDBInterface
            .getPopularMoviesList(API_KEY);
        break;
      case TOP_RATED:
        moviesRequest = movieDBInterface.getTopRatedMoviesList(API_KEY);
        break;
      default:
        moviesRequest = movieDBInterface
            .getPopularMoviesList(API_KEY);
        break;
    }

    moviesRequest.enqueue(new Callback<ServerResponse>() {
      @Override
      public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        Log.d(TAG, "onResponse: Call: " + call);
        Log.d(TAG, "onResponse: response: " + response);
        if (response.isSuccessful()) {
          ServerResponse serverResponse = response.body();
          List<Movie> moviesList;
          if (serverResponse != null) {
            moviesList = serverResponse.getMovies();
            mMoviesAdapter.setMoviesList(moviesList);
            mRecyclerView.smoothScrollToPosition(0);
          } else {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            Log.w(TAG, "onResponse: Server Response = null");
          }
        } else {
          Log.w(TAG, "onResponse: Response unsuccessful with code: " + response.code());
        }

      }

      @Override
      public void onFailure(Call<ServerResponse> call, Throwable t) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        Toast.makeText(MainActivity.this, R.string.error_unknown,
            Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onFailure: Throwable: " + t.getMessage());
      }
    });
  }

  /**
   * This method is overridden by our MainActivity class in order to handle RecyclerView item
   * clicks.
   *
   * @param movie The movie that was clicked
   */
  @Override
  public void onClick(Movie movie) {
    Intent intent = new Intent(this, DetailActivity.class);
    intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);
    startActivity(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
    MenuInflater inflater = getMenuInflater();
    /* Use the inflater's inflate method to inflate our menu layout to this menu */
    inflater.inflate(R.menu.main, menu);
    /* Return true so that the menu is displayed in the Toolbar */
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case R.id.menu_sort_by_popularity:
        //Check if there is Internet connectivity
        new InternetCheck(new Consumer() {
          @Override
          public void onConnectivityCheck(Boolean isConnected) {
            if (!isConnected) {
              Toast.makeText(MainActivity.this, R.string.error_no_internet,
                  Toast.LENGTH_LONG).show();
            } else {
              item.setChecked(true);
              loadMoviesData(mDataBaseInterface, SortedBy.POPULARITY);
            }
          }
        });
        return true;
      case R.id.menu_sort_by_rating:
        //Check if there is Internet connectivity
        new InternetCheck(new Consumer() {
          @Override
          public void onConnectivityCheck(Boolean isConnected) {
            if (!isConnected) {
              Toast.makeText(MainActivity.this, R.string.error_no_internet,
                  Toast.LENGTH_LONG).show();
            } else {
              item.setChecked(true);
              loadMoviesData(mDataBaseInterface, SortedBy.TOP_RATED);
            }
          }
        });
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
