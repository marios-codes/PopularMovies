package com.example.android.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.MoviesAdapter.MoviesAdapterViewHolder;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapterViewHolder> {

  /**
   * The interface that receives onClick messages.
   */
  public interface MoviesAdapterOnClickHandler {

    void onClick(Movie movie);
  }

  /*
   * An on-click handler to make it easy for an Activity to interface with the RecyclerView
   */
  private final MoviesAdapterOnClickHandler mClickHandler;

  private List<Movie> mMovieList = new ArrayList<>();

  /**
   * Creates a MoviesAdapter.
   *
   * @param clickHandler The on-click handler for this adapter. This single handler is called
   * when an item is clicked.
   */
  public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
    mClickHandler = clickHandler;
  }

  @NonNull
  @Override
  public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.movie_item, parent, false);
    return new MoviesAdapterViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull MoviesAdapterViewHolder holder, int position) {
    String moviePosterUrl = mMovieList.get(position).getPosterPath();
    Picasso.get().load(moviePosterUrl).into(holder.mPosterImageView);
  }

  @Override
  public int getItemCount() {
    if (null == mMovieList) return 0;
    return mMovieList.size();
  }

  /**
   * This method is used to set the moviesList on a MoviesAdapter if we've already
   * created one. This is handy when we get new data from the web but don't want to create a
   * new MoviesAdapter to display it or when the user changes his sorting preference.
   *
   * @param moviesList The new moviesList fetched from MovieDB to be displayed.
   */

  public void setMoviesList(List<Movie> moviesList) {
    mMovieList = moviesList;
    notifyDataSetChanged();
  }

  public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements
      OnClickListener {

    public final ImageView mPosterImageView;

    public MoviesAdapterViewHolder(View view) {
      super(view);
      mPosterImageView = view.findViewById(R.id.iv_poster);
      view.setOnClickListener(this);
    }

    /**
     * This gets called by the child views during a click.
     *
     * @param v The View that was clicked
     */
    @Override
    public void onClick(View v) {
      Movie movie = mMovieList.get(getAdapterPosition());
      mClickHandler.onClick(movie);
    }
  }
}
