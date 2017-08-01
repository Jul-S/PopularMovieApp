package com.example.android.popularmovieapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements MoviesRecyclerViewAdapter.MoviesOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MoviesRecyclerViewAdapter mMoviesAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String sorting;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    // for managing RecyclerView state;
    private Parcelable mListState;

    //for loading more pages from url
    private int currentPage = 1;
    private int totalPages;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mMoviesAdapter = new MoviesRecyclerViewAdapter(this);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mRecyclerView.setAdapter(mMoviesAdapter);
        // setting different LayoutManager for horizontal/vertical orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(mLayoutManager);
        } else {
            mLayoutManager = new GridLayoutManager(this, 4);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
        // Handling Pagination
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = mRecyclerView.getLayoutManager().getChildCount();
                int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
                int firstVisibleItemPosition = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) + 2 >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadMoreItems();
                    }
                }
            }
        });

        if (savedInstanceState == null) {
            if (sorting == null) sorting = "popular";
            loadMovieData(sorting, currentPage);
        }

    }

    private void loadMoreItems() {
        if (currentPage <= totalPages) {
            isLoading = true;
            currentPage += 1;
            Log.d(TAG, "Loading more pages " + currentPage);
            loadMovieData(sorting, currentPage);
        } else {
            isLastPage = true;
        }
    }

    private void loadMovieData(String sorting, int pageNum) {
        Log.d(TAG, "Loading pages " + pageNum);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        new FetchMoviesTask(this, new FetchMovieTaskCompleteListener()).execute(sorting, String.valueOf(pageNum));
    }

    @Override
    public void onClick(MovieData movieData) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        Bundle bundle = new Bundle();
        bundle.putSerializable("value", movieData);
        intentToStartDetailActivity.putExtras(bundle);
        startActivity(intentToStartDetailActivity);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_popular) {
            sorting = "popular";
            currentPage = 1;
            mMoviesAdapter.resetMovieData();
            mLayoutManager.scrollToPosition(0);
            loadMovieData(sorting, currentPage);
            return true;
        }

        if (id == R.id.action_toprated) {
            sorting = "top_rated";
            currentPage = 1;
            mMoviesAdapter.resetMovieData();
            mLayoutManager.scrollToPosition(0);
            loadMovieData(sorting, currentPage);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onSaveInstance");
        mListState = mLayoutManager.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putString("sort", sorting);
        bundle.putInt("total_pages", totalPages);
        bundle.putInt("curr_page", currentPage);
        bundle.putParcelable("state", mListState);
        bundle.putSerializable("data", mMoviesAdapter.getMovieData());

        savedInstanceState.putBundle("state_bundle", bundle);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if ((savedInstanceState != null) &&
                (savedInstanceState.getBundle("state_bundle") != null) &&
                savedInstanceState.getBundle("state_bundle").getSerializable("data") != null) {
            Bundle bundle = savedInstanceState.getBundle("state_bundle");
            sorting = bundle.getString("sort");
            totalPages = bundle.getInt("total_pages");
            currentPage = bundle.getInt("curr_page");
            mListState = bundle.getParcelable("state");
            MovieData[] data = (MovieData[]) bundle.getSerializable("data");
            mMoviesAdapter.resetMovieData();
            mMoviesAdapter.addMovieData(data);
        }
    }

    // this section for debuging lifecycle only
    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

    }

    public class FetchMovieTaskCompleteListener implements AsyncTaskCompleteListener<MovieData> {
        @Override
        public void onTaskComplete(MovieData[] movieData, int pages) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                isLoading = false;
                totalPages = pages;
                showMovieDataView();
                mMoviesAdapter.addMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }
    }
}
