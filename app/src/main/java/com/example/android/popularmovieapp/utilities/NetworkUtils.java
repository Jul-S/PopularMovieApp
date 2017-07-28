package com.example.android.popularmovieapp.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovieapp.BuildConfig;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by yul on 27.07.17.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_DB_URL =
            "http://api.themoviedb.org/3/movie/";

    private static final String IMAGE_BASE_URL =
            "http://image.tmdb.org/t/p/";


    private static final String APPI_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";
    private static final String imageSize = "w185";

    public static URL buildUrl(String sortingQuery, String pageQuery) {
        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(sortingQuery)
                .appendQueryParameter(APPI_PARAM, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                .appendQueryParameter(PAGE_PARAM,pageQuery)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildImageUrl(String imageId) {
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(imageSize)
                .appendPath(imageId.substring(1))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built Image URI " + url);

        return url;
    }
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }

        } finally {
            urlConnection.disconnect();
        }
    }
}
