package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.android.popularmovies.Models.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

  //intent extras
  public final static String EXTRA_MOVIE = "intent.extra.movie";
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.tv_detail_title) TextView titleTV;
  @BindView(R.id.tv_detail_rating) TextView ratingTV;
  @BindView(R.id.tv_detail_release_date) TextView releaseDateTV;
  @BindView(R.id.tv_detail_synopsis) TextView synopsisTV;
  @BindView(R.id.iv_detail_poster) ImageView posterIV;
  @BindView(R.id.iv_expanded_poster) ImageView backDropIV;


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

}
