package com.example.android.popularmovies.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.android.popularmovies.Adapters.ReviewsAdapter.ReviewsAdapterViewHolder;
import com.example.android.popularmovies.Models.Review;
import com.example.android.popularmovies.R;
import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapterViewHolder> {

  private static final String TAG = ReviewsAdapter.class.getSimpleName();

  private List<Review> mReviewList = new ArrayList<>();

  public ReviewsAdapter() {
  }

  @NonNull
  @Override
  public ReviewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.review_item, parent, false);
    return new ReviewsAdapterViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ReviewsAdapterViewHolder holder, int position) {
    Review review = mReviewList.get(position);

    holder.author.setText(review.getAuthor());
    holder.reviewText.setText(review.getContent());
  }

  @Override
  public int getItemCount() {
    if (null == mReviewList) return 0;
    return mReviewList.size();
  }

  public void setReviewsList (List<Review> reviewList) {
    mReviewList = reviewList;
    notifyDataSetChanged();
  }

  public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_review_name)
    TextView author;
    @BindView(R.id.tv_review_content)
    TextView reviewText;

    public ReviewsAdapterViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
