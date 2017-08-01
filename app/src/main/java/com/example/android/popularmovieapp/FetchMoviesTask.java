package com.example.android.popularmovieapp;

import android.content.Context;
import android.os.AsyncTask;

import com.example.android.popularmovieapp.utilities.JSONParserUtils;
import com.example.android.popularmovieapp.utilities.NetworkUtils;

import java.net.URL;


class FetchMoviesTask extends AsyncTask<String, Void, MovieData[]> {
    private Context context;
    private AsyncTaskCompleteListener<MovieData> listener;
    private int totalPages;

    public FetchMoviesTask(Context context, AsyncTaskCompleteListener<MovieData> listener) {
        this.context = context;
        this.listener = listener;
    }

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
    protected void onPostExecute(MovieData[] data) {
        super.onPostExecute(data);
        listener.onTaskComplete(data, totalPages);
    }
}
