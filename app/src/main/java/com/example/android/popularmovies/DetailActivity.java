package com.example.android.popularmovies;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.android.popularmovies.Adapters.ReviewsAdapter;
import com.example.android.popularmovies.Database.AppDatabase;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Models.RetrofitResponse.ReviewsResponse;
import com.example.android.popularmovies.Models.RetrofitResponse.TrailersResponse;
import com.example.android.popularmovies.Models.Review;
import com.example.android.popularmovies.Models.Trailer;
import com.example.android.popularmovies.Network.MovieDBInterface;
import com.example.android.popularmovies.Network.MovieDBUtils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

  //intent extras
  public final static String EXTRA_MOVIE = "intent.extra.movie";
  private static final String TAG = DetailActivity.class.getSimpleName();

  //MovieDB trailer type of video
  private static final String TYPE_TRAILER = "Trailer";

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.tv_detail_title)
  TextView titleTV;
  @BindView(R.id.tv_detail_rating)
  TextView ratingTV;
  @BindView(R.id.tv_detail_release_date)
  TextView releaseDateTV;
  @BindView(R.id.tv_detail_synopsis)
  TextView synopsisTV;
  @BindView(R.id.iv_detail_poster)
  ImageView posterIV;
  @BindView(R.id.iv_expanded_poster)
  ImageView backDropIV;
  @BindView(R.id.iv_detail_play_icon)
  ImageView trailerPlayIV;
  @BindView(R.id.detail_trailer_play_label)
  TextView trailerPlayLabelTV;
  @BindView(R.id.pb_detail_loading_trailer)
  ProgressBar trailerLoadingIndicator;
  @BindView(R.id.rv_detail_reviews)
  RecyclerView mReviewsRecycler;
  @BindView(R.id.tv_detail_reviews_label)
  TextView reviewLabelTV;
  @BindView(R.id.pb_detail_loading_reviews)
  ProgressBar reviewsLoadingIndicator;
  @BindView(R.id.fab_detail)
  FloatingActionButton favFab;

  private ReviewsAdapter mReviewsAdapter;
  private MovieDBInterface mMovieDBInterface;
  private String mTrailerUrl;
  private AppDatabase mDb;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);

    ButterKnife.bind(this);

    setSupportActionBar(toolbar);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    setTitle(null);

    //get intent and check for null
    Intent intent = getIntent();
    if (intent == null) {
      closeOnError();
      return;
    }

    Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);
    if (movie != null) {
      initViews(movie);
      mMovieDBInterface = MovieDBUtils.setupMovieDbInterface();
      makeCallForTrailer(movie.getId());
      makeCallForReviews(movie.getId());
    } else {
      closeOnError();
    }
  }

  private void initViews(final Movie movie) {
    titleTV.setText(movie.getTitle());
    ratingTV.setText(getResources()
        .getString(R.string.detail_movie_rating, String.valueOf(movie.getUserRating())));
    releaseDateTV.setText(
        getResources().getString(R.string.detail_movie_release_date, movie.getReleaseDate()));
    synopsisTV.setText(movie.getPlotSynopsis());

    //init database or connect with the initialized one
    mDb = AppDatabase.getInstance(getApplicationContext());

    //setup Fav Floating Action Button
    AppExecutors.getInstance().diskIO().execute(new Runnable() {
      @Override
      public void run() {
        Movie favoriteMovie = mDb.movieDAO().findMovieWithID(movie.getId());
        if (favoriteMovie != null && favoriteMovie.getId().equals(movie.getId())) {
          //if user has marked movie as favorite change
          //fab star color to yellow
          ImageViewCompat.setImageTintList(
              favFab,
              ColorStateList
                  .valueOf(ContextCompat.getColor(DetailActivity.this, R.color.fab_favorite_true))
          );
        }
      }
    });
    //setup fab on click listener
    favFab.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(final View view) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
          @Override
          public void run() {
            //check if movie already in favorites
            Movie movieInFavorites = mDb.movieDAO().findMovieWithID(movie.getId());
            if (movieInFavorites == null) {
              //movie doesn't exist in favorites
              mDb.movieDAO().insertFavoriteMovie(movie);
              //change fab star color to yellow
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  ImageViewCompat.setImageTintList(
                      favFab,
                      ColorStateList
                          .valueOf(ContextCompat
                              .getColor(DetailActivity.this, R.color.fab_favorite_true))
                  );
                  Snackbar.make(view, getString(R.string.detail_snackbar_added_to_favorites),
                      Snackbar.LENGTH_SHORT).show();
                }
              });
            } else {
              //Movie already exists in favorites, so remove it
              mDb.movieDAO().deleteFavoriteMovie(movie);
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  //change fab star color to default white
                  ImageViewCompat.setImageTintList(
                      favFab,
                      ColorStateList
                          .valueOf(ContextCompat
                              .getColor(DetailActivity.this, R.color.fab_favorite_false))
                  );
                  Snackbar.make(view, getString(R.string.detail_snackbar_removed_from_favorites),
                      Snackbar.LENGTH_SHORT).show();
                }
              });
            }
          }
        });
      }
    });

    Picasso.get()
        .load(movie.getPosterPath())
        .placeholder(R.drawable.ic_popcorn_placeholder)
        .into(posterIV);

    Picasso.get()
        .load(movie.getBackdropPath())
        .placeholder(R.drawable.ic_popcorn_placeholder)
        .into(backDropIV);

    //setup reviews recyclerview
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    mReviewsRecycler.setLayoutManager(layoutManager);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
        layoutManager.getOrientation());
    mReviewsRecycler.addItemDecoration(dividerItemDecoration);
    mReviewsRecycler.setNestedScrollingEnabled(false); //for smooth scrolling
    mReviewsRecycler.setHasFixedSize(true);
    mReviewsAdapter = new ReviewsAdapter();
    mReviewsRecycler.setAdapter(mReviewsAdapter);
  }

  private void makeCallForTrailer(Integer movieId) {
    Call<TrailersResponse> trailersRequest = mMovieDBInterface
        .getMovieTrailers(movieId, MainActivity.API_KEY);

    trailersRequest.enqueue(new Callback<TrailersResponse>() {
      @Override
      public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
        if (response.isSuccessful()) {
          TrailersResponse trailersResponse = response.body();
          List<Trailer> trailerList;
          if (trailersResponse != null) {
            trailerList = trailersResponse.getTrailerList();
            //check only for trailer video, and no other kind of video
            ArrayList<String> trailerURLsList = new ArrayList<>();
            for (Trailer trailer : trailerList) {
              if (trailer.getType().equals(TYPE_TRAILER)) {
                //populate the array with trailer URLs duh
                trailerURLsList.add(trailer.getUrl());
              }
            }
            //We only care for one trailer, so let's take the first element of the
            //trailerURLsList array, if it has at least one element
            if (!trailerURLsList.isEmpty()) {
              mTrailerUrl = trailerURLsList.get(0);
              //The trailer list is not empty and we have fetched the video's YouTube URL
              //So let's make the trailer play button and label visible (invisible by default)
              trailerPlayIV.setVisibility(View.VISIBLE);
              trailerPlayLabelTV.setVisibility(View.VISIBLE);
              //hide progress bar
              trailerLoadingIndicator.setVisibility(View.INVISIBLE);
              Log.d(TAG, "onResponse: trailerUrl: " + mTrailerUrl);
            } else {
              //no trailers available
              Log.d(TAG, "onResponse: no trailers available");
              //hide progress bar and update textview label to inform that no trailers are available
              trailerLoadingIndicator.setVisibility(View.INVISIBLE);
              trailerPlayLabelTV.setText(R.string.detail_trailer_label_not_available);
              trailerPlayLabelTV.setVisibility(View.VISIBLE);
            }

          } else {
            Log.w(TAG, "onResponse: Server Response = null");
            //hide progress bar and update textview label to inform that no trailers are available
            trailerLoadingIndicator.setVisibility(View.INVISIBLE);
            trailerPlayLabelTV.setText(R.string.detail_trailer_label_not_available);
            trailerPlayLabelTV.setVisibility(View.VISIBLE);
            Toast.makeText(DetailActivity.this, R.string.detail_trailer_fetch_error,
                Toast.LENGTH_SHORT)
                .show();
          }
        } else {
          Log.w(TAG, "Trailer onResponse: Response unsuccessful with code: " + response.code());
          //hide progress bar and update textview label to inform that no trailers are available
          trailerLoadingIndicator.setVisibility(View.INVISIBLE);
          trailerPlayLabelTV.setText(R.string.detail_trailer_label_not_available);
          trailerPlayLabelTV.setVisibility(View.VISIBLE);
          Toast.makeText(DetailActivity.this, R.string.detail_trailer_fetch_error,
              Toast.LENGTH_SHORT)
              .show();
        }
      }

      @Override
      public void onFailure(Call<TrailersResponse> call, Throwable t) {
        //hide progress bar and update textview label to inform that no trailers are available
        trailerLoadingIndicator.setVisibility(View.INVISIBLE);
        trailerPlayLabelTV.setText(R.string.detail_trailer_label_not_available);
        trailerPlayLabelTV.setVisibility(View.VISIBLE);
        Toast.makeText(DetailActivity.this, R.string.detail_trailer_fetch_error, Toast.LENGTH_SHORT)
            .show();
        Log.e(TAG, "onTrailerFailure: Throwable: " + t.getMessage());
      }
    });
  }

  private void makeCallForReviews(Integer movieId) {
    Call<ReviewsResponse> reviewsRequest = mMovieDBInterface
        .getMovieReviews(movieId, MainActivity.API_KEY);

    reviewsRequest.enqueue(new Callback<ReviewsResponse>() {
      @Override
      public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
        if (response.isSuccessful()) {
          ReviewsResponse reviewsResponse = response.body();
          List<Review> reviewList;
          if (reviewsResponse != null) {
            reviewList = reviewsResponse.getReviewList();
            if (!reviewList.isEmpty()) {
              mReviewsAdapter.setReviewsList(reviewList);
              //hide progress bar and set recycler view and reviews label to visible
              //Additionally change the review label text to "reviews" from default "No reviews available"
              reviewsLoadingIndicator.setVisibility(View.INVISIBLE);
              mReviewsRecycler.setVisibility(View.VISIBLE);
              reviewLabelTV.setText(R.string.detail_reviews_label);
              reviewLabelTV.setVisibility(View.VISIBLE);
            } else {
              //review list is empty
              //hide the progress bar and make review label textview visible (default message: "No reviews available")
              reviewsLoadingIndicator.setVisibility(View.INVISIBLE);
              reviewLabelTV.setVisibility(View.VISIBLE);
            }
          } else {
            Log.w(TAG, "Review onResponse: Server Response = null");
            //hide the progress bar and make review label textview visible (default message: "No reviews available")
            reviewsLoadingIndicator.setVisibility(View.INVISIBLE);
            reviewLabelTV.setVisibility(View.VISIBLE);
            Toast.makeText(DetailActivity.this, R.string.detail_review_fetch_error,
                Toast.LENGTH_SHORT)
                .show();
          }
        } else {
          Log.w(TAG, "Review onResponse: Response unsuccessful with code: " + response.code());
          //hide the progress bar and make review label textview visible (default message: "No reviews available")
          reviewsLoadingIndicator.setVisibility(View.INVISIBLE);
          reviewLabelTV.setVisibility(View.VISIBLE);
          Toast
              .makeText(DetailActivity.this, R.string.detail_review_fetch_error, Toast.LENGTH_SHORT)
              .show();
        }
      }

      @Override
      public void onFailure(Call<ReviewsResponse> call, Throwable t) {
        //hide the progress bar and make review label textview visible (default message: "No reviews available")
        reviewsLoadingIndicator.setVisibility(View.INVISIBLE);
        reviewLabelTV.setVisibility(View.VISIBLE);
        Toast.makeText(DetailActivity.this, R.string.detail_review_fetch_error, Toast.LENGTH_SHORT)
            .show();
        Log.e(TAG, "onReviewFailure: Throwable: " + t.getMessage());
      }
    });

  }

  private void closeOnError() {
    finish();
    Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void playTrailer(View view) {
    if (mTrailerUrl != null && !mTrailerUrl.isEmpty()) {
      Intent playTrailerIntent = new Intent(Intent.ACTION_VIEW);
      playTrailerIntent.setData(Uri.parse(Uri.decode(mTrailerUrl)));
      startActivity(playTrailerIntent);
    }
  }
}
