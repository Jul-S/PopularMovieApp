package com.example.android.popularmovieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private MovieData data;
    private TextView titleTV;
    private ImageView posterIV;
    private TextView ratingTV;
    private TextView descTV;
    private TextView releaseTV;
    private static final String TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        titleTV = (TextView) findViewById(R.id.tv_title);
        posterIV = (ImageView) findViewById(R.id.iv_movie_poster);
        ratingTV = (TextView) findViewById(R.id.tv_rating);
        descTV = (TextView) findViewById(R.id.tv_desc);
        releaseTV = (TextView) findViewById(R.id.tv_release);
        Intent intent = this.getIntent();


        if (intent.hasExtra("value")) {
            Bundle bundle = intent.getExtras();
            data = (MovieData) bundle.getSerializable("value");
        } else {
            Log.d(TAG, "Intent extra is NULL");
        }

        if (data != null) {
            titleTV.setText(data.getTitle());
            Picasso.with(this)
                    .load(data.getImageUrl().toString())
                    .centerCrop().fit()
                    .into(posterIV);
            ratingTV.setText(String.valueOf(data.getRating()));
            descTV.setText(data.getDescription());
            releaseTV.setText(data.getReleaseDate());
        } else {
            Log.d(TAG, "movie data is NULL");
        }
    }

}
