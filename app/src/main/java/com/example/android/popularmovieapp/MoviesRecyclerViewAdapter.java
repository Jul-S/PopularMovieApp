package com.example.android.popularmovieapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yul on 25.07.17.
 */

public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.MovieViewHolder> {
    private final MoviesOnClickHandler mClickHandler;
    private MovieData[] mMoviesData;

    public MoviesRecyclerViewAdapter(MoviesOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public void resetMovieData() {
        mMoviesData = null;
    }

    public interface MoviesOnClickHandler {
        void onClick(MovieData movieData);
    }

    public void addMovieData(MovieData[] moviesData) {
        if (mMoviesData != null) {
            List<MovieData> copy = new ArrayList<>(Arrays.asList(mMoviesData));
            copy.addAll(Arrays.asList(moviesData));
            mMoviesData = copy.toArray(new MovieData[copy.size()]);
        } else mMoviesData = moviesData;
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movies_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if (null != mMoviesData) {
            MovieData curMovieData = mMoviesData[position];
            Context context = holder.mPosterImageView.getContext();
            holder.mLoadingIndicator.setVisibility(View.INVISIBLE);
            Picasso.with(context)
                    .load(curMovieData.getImageUrl().toString())
                    .centerCrop().fit()
                    .into(holder.mPosterImageView);
        } else {
            holder.mLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mMoviesData) return 0;
        return mMoviesData.length;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mPosterImageView;
        private final ProgressBar mLoadingIndicator;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.iv_movie_poster);
            mLoadingIndicator = itemView.findViewById(R.id.pb_loading_indicator);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieData movieData = mMoviesData[adapterPosition];
            mClickHandler.onClick(movieData);
        }
    }

}
