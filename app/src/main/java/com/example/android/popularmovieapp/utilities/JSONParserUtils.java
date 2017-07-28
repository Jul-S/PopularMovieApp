package com.example.android.popularmovieapp.utilities;

import com.example.android.popularmovieapp.MovieData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by yul on 27.07.17.
 */

public class JSONParserUtils {

    public static MovieData[] getMoviesFromJson(String movieJsonString) throws JSONException {
        final String MESSAGE_CODE = "cod";
        final String LIST = "results";

        MovieData[] movies;

        JSONObject moviesJson = new JSONObject(movieJsonString);

        /* Is there an error? */
        if (moviesJson.has(MESSAGE_CODE)) {
            int errorCode = moviesJson.getInt(MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        // getting json movies array from json string
        JSONArray moviesArray = moviesJson.getJSONArray(LIST);
        movies = new MovieData[moviesArray.length()];

        //collecting data from json array of movies
        for (int i = 0; i < moviesArray.length(); i++) {

            /* Get the JSON object representing the day */
            JSONObject singleMovieData = moviesArray.getJSONObject(i);
            String title = singleMovieData.getString("title");
            String description = singleMovieData.getString("overview");
            String imageUrl = singleMovieData.getString("poster_path");
            double rating = singleMovieData.getDouble("vote_average");
            String releaseDate = singleMovieData.getString("release_date");
            movies[i] = new MovieData(title,description,imageUrl,rating,releaseDate);

        }

        return movies;
    }

    public static int getTotalPages(String jsonMovieString) throws JSONException {
        JSONObject moviesJson = new JSONObject(jsonMovieString);
        return moviesJson.getInt("total_pages");
    }
}
