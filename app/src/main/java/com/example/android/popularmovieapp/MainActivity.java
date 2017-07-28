package com.example.android.popularmovieapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovieapp.utilities.JSONParserUtils;
import com.example.android.popularmovieapp.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MoviesRecyclerViewAdapter.MoviesOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MoviesRecyclerViewAdapter mMoviesAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String sorting;
    private TextView mErrorMessageDisplay;

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
        if (savedInstanceState != null) {
            sorting = savedInstanceState.getString("sort");
            mListState = savedInstanceState.getParcelable("state");
            mLayoutManager.onRestoreInstanceState(mListState);
        }
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        // changing number of coloms in horizontal orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(mLayoutManager);
        } else {
            mLayoutManager = new GridLayoutManager(this, 4);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }

        mMoviesAdapter = new MoviesRecyclerViewAdapter(this);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mRecyclerView.setAdapter(mMoviesAdapter);

        // Hadling Pagination
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


        if (sorting == null) sorting = "popular";
        loadMovieData(sorting, currentPage);

    }

    private void loadMoreItems() {
        if (currentPage <= totalPages) {
            isLoading = true;
            currentPage += 1;
            Log.d(TAG, "Loading more pages " + currentPage);
            loadMovieData(sorting, currentPage);
        } else isLastPage = true;
    }

    private void loadMovieData(String sorting, int pageNum) {
        Log.d(TAG, "Loading pages " + pageNum);
        new FetchMoviesTask().execute(sorting, String.valueOf(pageNum));
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

    private class FetchMoviesTask extends AsyncTask<String, Void, MovieData[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MovieData[] doInBackground(String... strings) {
            if (strings.length < 2) return null;
            String sort = strings[0];
            String page = strings[1];
            URL moviesRequestUrl = NetworkUtils.buildUrl(sort, page);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);
                totalPages = JSONParserUtils.getTotalPages(jsonMovieResponse);

                return JSONParserUtils
                        .getMoviesFromJson(jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieData[] movieData) {
            if (movieData != null) {
                isLoading = false;
                showMovieDataView();
                mMoviesAdapter.addMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }
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
        savedInstanceState.putString("sort", sorting);
        savedInstanceState.putParcelable("state", mListState);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstance");
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            sorting = savedInstanceState.getString("sort");
            mListState = savedInstanceState.getParcelable("state");
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
}
