package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Models.RetrofitResponse.TrailersResponse;
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

  private MovieDBInterface mMovieDBInterface;
  private String mTrailerUrl;

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
    } else {
      closeOnError();
    }
  }

  private void initViews(Movie movie) {
    titleTV.setText(movie.getTitle());
    ratingTV.setText(getResources()
        .getString(R.string.detail_movie_rating, String.valueOf(movie.getUserRating())));
    releaseDateTV.setText(
        getResources().getString(R.string.detail_movie_release_date, movie.getReleaseDate()));
    synopsisTV.setText(movie.getPlotSynopsis());

    Picasso.get()
        .load(movie.getPosterPath())
        .placeholder(R.drawable.ic_popcorn_placeholder)
        .into(posterIV);

    Picasso.get()
        .load(movie.getBackdropPath())
        .placeholder(R.drawable.ic_popcorn_placeholder)
        .into(backDropIV);
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
            Toast.makeText(DetailActivity.this, R.string.detail_trailer_label_error, Toast.LENGTH_SHORT)
                .show();
          }
        } else {
          Log.w(TAG, "Trailer onResponse: Response unsuccessful with code: " + response.code());
          //hide progress bar and update textview label to inform that no trailers are available
          trailerLoadingIndicator.setVisibility(View.INVISIBLE);
          trailerPlayLabelTV.setText(R.string.detail_trailer_label_not_available);
          trailerPlayLabelTV.setVisibility(View.VISIBLE);
          Toast.makeText(DetailActivity.this, R.string.detail_trailer_label_error, Toast.LENGTH_SHORT)
              .show();
        }
      }

      @Override
      public void onFailure(Call<TrailersResponse> call, Throwable t) {
        //hide progress bar and update textview label to inform that no trailers are available
        trailerLoadingIndicator.setVisibility(View.INVISIBLE);
        trailerPlayLabelTV.setText(R.string.detail_trailer_label_not_available);
        trailerPlayLabelTV.setVisibility(View.VISIBLE);
        Toast.makeText(DetailActivity.this, R.string.detail_trailer_label_error, Toast.LENGTH_SHORT)
            .show();
        Log.e(TAG, "onFailure: Throwable: " + t.getMessage());
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
