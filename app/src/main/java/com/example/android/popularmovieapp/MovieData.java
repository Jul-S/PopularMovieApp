package com.example.android.popularmovieapp;

import com.example.android.popularmovieapp.utilities.NetworkUtils;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by yul on 27.07.17.
 */

public class MovieData implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String title;
    private final String description;
    private final String imageUrl;
    private final double rating;
    private final String releaseDate;

    public MovieData(String title, String description, String imageUrl, double rating, String releaseDate) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public URL getImageUrl() {
        return NetworkUtils.buildImageUrl(imageUrl);
    }

    public String getDescription() {
        return description;
    }

    public double getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
