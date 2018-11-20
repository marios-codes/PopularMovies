package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.android.popularmovies.Adapters.MoviesAdapter;
import com.example.android.popularmovies.Adapters.MoviesAdapter.MoviesAdapterOnClickHandler;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Network.InternetCheck;
import com.example.android.popularmovies.Network.InternetCheck.Consumer;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapterOnClickHandler {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final String SAVED_LIST_POSITION_KEY = "list-position";
  private static final String SAVED_PREFERRED_SORTING_KEY = "sorting-type";
  private static final String SAVED_MENU_ITEM_ID_KEY = "menu-item-id";

  private static final int GRID_SPAN_COUNT = 2;

  private enum SortedBy {POPULARITY, TOP_RATED, FAVORITES}

  private SortedBy mSortedBy = SortedBy.POPULARITY;
  private int mSelectedMenuItemID = -1;

  public static final String API_KEY = BuildConfig.ApiKey;
  @BindView(R.id.recyclerview_movies)
  RecyclerView mRecyclerView;
  @BindView(R.id.pb_loading_indicator)
  ProgressBar mLoadingIndicator;
  private MainViewModel mainViewModel;
  private MoviesAdapter mMoviesAdapter;
  private Bundle mSavedInstanceState;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    mSavedInstanceState = savedInstanceState;
    initViews();

    //get preferred sorting order in case the activity is recreated due to a configuration change
    if (mSavedInstanceState != null) {
      if (mSavedInstanceState.containsKey(SAVED_PREFERRED_SORTING_KEY)) {
        mSortedBy = (SortedBy) mSavedInstanceState
            .getSerializable(SAVED_PREFERRED_SORTING_KEY);
      }
    }

    /*
    check if this part runs from an orientation change where favorite movies sorting was selected
    so that we run the setupViewModelForFavoriteMovies() method even if no internet connection
    is available
    */
    if (mSortedBy.equals(SortedBy.FAVORITES)) {
      setupViewModelForFavoriteMovies();
      return;
    }

    //Check if there is Internet connectivity to load popular or top rated movies
    new InternetCheck(new Consumer() {
      @Override
      public void onConnectivityCheck(Boolean isConnected) {
        if (!isConnected) {
          Toast.makeText(MainActivity.this, R.string.error_no_internet,
              Toast.LENGTH_LONG).show();
        } else {
          if (mSortedBy.equals(SortedBy.POPULARITY)) {
            setupViewModelForPopularMovies();
          } else {
            setupViewModelForTopRatedMovies();
          }
        }
      }
    });
  }

  private void initViews() {
    GridLayoutManager mLayoutManager = new GridLayoutManager(MainActivity.this, GRID_SPAN_COUNT);
    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.setHasFixedSize(true);

    mMoviesAdapter = new MoviesAdapter(this);
    mRecyclerView.setAdapter(mMoviesAdapter);

  }

  private void setupViewModelForPopularMovies() {
    mainViewModel.getPopularMovies().observe(this, new Observer<List<Movie>>() {
      @Override
      public void onChanged(@Nullable List<Movie> movieList) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (movieList == null) {
          Toast.makeText(MainActivity.this, R.string.error_unknown,
              Toast.LENGTH_SHORT).show();
        } else {
          mMoviesAdapter.setMoviesList(movieList);
          scrollListToSavedPosition();
        }
      }
    });
  }

  private void setupViewModelForTopRatedMovies() {
    mainViewModel.getTopRatedMovies().observe(this, new Observer<List<Movie>>() {
      @Override
      public void onChanged(@Nullable List<Movie> movieList) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (movieList == null) {
          Toast.makeText(MainActivity.this, R.string.error_unknown,
              Toast.LENGTH_SHORT).show();
        } else {
          mMoviesAdapter.setMoviesList(movieList);
          scrollListToSavedPosition();
        }
      }
    });
  }

  private void setupViewModelForFavoriteMovies() {
    mainViewModel.getFavoriteMovies(MainActivity.this).observe(this, new Observer<List<Movie>>() {
      @Override
      public void onChanged(@Nullable List<Movie> movies) {
        if (movies != null) {
          mMoviesAdapter.setMoviesList(movies);
          scrollListToSavedPosition();
        } else {
          Toast.makeText(MainActivity.this, R.string.empty_favorite_list_toast, Toast.LENGTH_SHORT)
              .show();
        }
      }
    });
  }

  private void scrollListToSavedPosition() {
    if (mSavedInstanceState != null && mSavedInstanceState
        .containsKey(SAVED_LIST_POSITION_KEY)) {
      if (mSavedInstanceState.containsKey(SAVED_PREFERRED_SORTING_KEY) && mSavedInstanceState.getSerializable(SAVED_PREFERRED_SORTING_KEY).equals(mSortedBy)) {
        Parcelable savedRecyclerLayoutState = mSavedInstanceState
            .getParcelable(SAVED_LIST_POSITION_KEY);
        mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
      } else {
        mRecyclerView.scrollToPosition(0);
      }
    } else {
      mRecyclerView.scrollToPosition(0);
    }
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
    //check the menu item that was selected before orientation change
    if (mSavedInstanceState != null && mSavedInstanceState.containsKey(SAVED_MENU_ITEM_ID_KEY)) {
      mSelectedMenuItemID = mSavedInstanceState.getInt(SAVED_MENU_ITEM_ID_KEY);
      if (mSelectedMenuItemID != -1) { //default id assigned on first run
        MenuItem itemToBeChecked = menu.findItem(mSelectedMenuItemID);
        itemToBeChecked.setChecked(true);
      }
    }

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
              mSelectedMenuItemID = R.id.menu_sort_by_popularity;
              item.setChecked(true);
              mSortedBy = SortedBy.POPULARITY;
              setupViewModelForPopularMovies();
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
              mSelectedMenuItemID = R.id.menu_sort_by_rating;
              item.setChecked(true);
              mSortedBy = SortedBy.TOP_RATED;
              setupViewModelForTopRatedMovies();
            }
          }
        });
        return true;
      case R.id.menu_sort_by_favorites:
        mSelectedMenuItemID = R.id.menu_sort_by_favorites;
        item.setChecked(true);
        mSortedBy = SortedBy.FAVORITES;
        setupViewModelForFavoriteMovies();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
/* save the scroll position of the list and the preferred sorting type
    in order to retain it on a configuration change
    Also check for null, otherwise app will crash on orientation change with no internet connection
*/
    if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
      outState.putParcelable(SAVED_LIST_POSITION_KEY,
          mRecyclerView.getLayoutManager().onSaveInstanceState());
    }
    outState.putSerializable(SAVED_PREFERRED_SORTING_KEY, mSortedBy);
    //save the menu item that was checked
    outState.putInt(SAVED_MENU_ITEM_ID_KEY, mSelectedMenuItemID);
  }
}
