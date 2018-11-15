package com.example.android.moviesstage2;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MoviesDetailActivity extends AppCompatActivity {

    private static final String MOVIE_KEY = "MOVIE_KEY";

    private ImageView mPoster;
    private TextView mRating;
    private TextView mReleaseDate;
    private TextView mSynopsis;
    private String mMovieId;
    private LinearLayout mReview;
    private LinearLayout mTrailers;
    private RelativeLayout mRelativeLayout;
    private LinearLayoutManager mReviewLayout;
    private RecyclerView mReviewRecyclerView;
    private Button mButton;
    // private AppDatabase mAppDatabase;
    private boolean favorite;
    private MovieItem mMovie;
    private String[] mTrailerKeys;
    private String[] mTrailerNames;
    private String[] mReviewAuthors;
    private String[] mReviewContent;
    private final String TRAILER_BASE_URL = "http://youtube.com/watch?v=";
    private static final int MOVIE_LOADER_ID = 50;
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY = BuildConfig.API_KEY;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_detail);

        mMovie = (MovieItem) getIntent().getParcelableExtra(MOVIE_KEY);

        mPoster = (ImageView) findViewById(R.id.movie_poster_detail);
        mRating = (TextView) findViewById(R.id.movie_rating_detail);
        mReleaseDate = (TextView) findViewById(R.id.movie_release_date_detail);
        mSynopsis = (TextView) findViewById(R.id.movie_synopsis_detail);
        mReview = findViewById(R.id.movie_review_list);
        mTrailers = findViewById(R.id.movie_trailer_list);


        Picasso.with(this).load(mMovie.getPosterPath()).into(mPoster);
        mRating.setText(Double.toString(mMovie.getRating())+" out of 10 stars");
        mReleaseDate.setText("     " + mMovie.getReleaseDate());
        mSynopsis.setText(mMovie.getSynopsis());
        mMovieId = mMovie.getMovieId();

        new GetTrailers().execute();
        new GetReviews().execute();

    }

    public class GetTrailers extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL trailersUrl = NetworkUtils.buildTrailerUrl(String.valueOf(mMovieId));
                return NetworkUtils.getMovieInfo(trailersUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            getTrailerData(result);
            loadTrailers();
        }
    }

    public void getTrailerData(String trailerJsonResponse){
        try {
            JSONObject trailerObject = new JSONObject(trailerJsonResponse);
            JSONArray trailerArray = trailerObject.getJSONArray("results");
            mTrailerKeys = new String[trailerArray.length()];
            mTrailerNames = new String[trailerArray.length()];
            for (int i = 0; i < trailerArray.length(); i++){
                mTrailerKeys[i] = trailerArray.getJSONObject(i).optString("key");
                mTrailerNames[i] = trailerArray.getJSONObject(i).optString("name");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadTrailers(){
        if (mTrailerKeys.length == 0){
            TextView noTrailers = new TextView(this);
            noTrailers.setText("No Trailers Available");
            noTrailers.setTextSize(12);
            mTrailers.addView(noTrailers);

        }else {
            for (int i = 0; i < mTrailerKeys.length; i++){
                Button trailerItem = new Button(this);
                trailerItem.setText(mTrailerNames[i]);
                trailerItem.setTextSize(12);
                final String trailerUrl = TRAILER_BASE_URL + mTrailerKeys[i];
                trailerItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri youtubeLink = Uri.parse(trailerUrl);
                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, youtubeLink);
                        if (youtubeIntent.resolveActivity(getPackageManager()) != null){
                            startActivity(youtubeIntent);
                        }
                    }
                });
                mTrailers.addView(trailerItem);
            }

        }

    }

    public class GetReviews extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL reviewsUrl = NetworkUtils.buildReviewsUrl(String.valueOf(mMovieId));
                return NetworkUtils.getMovieInfo(reviewsUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            getReviewData(s);
            loadReviews();
        }
    }

    public void getReviewData(String reviewJsonResponse){
        try {
            JSONObject reviewObject = new JSONObject(reviewJsonResponse);
            JSONArray reviewArray = reviewObject.getJSONArray("results");
            mReviewAuthors = new String[reviewArray.length()];
            mReviewContent = new String[reviewArray.length()];
            for (int i = 0; i < reviewArray.length(); i++){
                mReviewAuthors[i] = reviewArray.getJSONObject(i).optString("author");
                mReviewContent[i] = reviewArray.getJSONObject(i).optString("content");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void loadReviews(){
        if (mReviewContent.length == 0){
            TextView noReviews = new TextView(this);
            noReviews.setText("No Reviews Available");
            noReviews.setTextSize(12);
            mReview.addView(noReviews);

        }else {
            for (int i = 0; i < mReviewContent.length; i++){
                TextView reviewAuthor = new TextView(this);
                TextView reviewContent = new TextView(this);
                reviewAuthor.setText("Author: " + mReviewAuthors[i]);
                reviewAuthor.setTextSize(14);
                reviewContent.setText(mReviewContent[i] + "\n\n\n");
                reviewContent.setTextSize(12);

                mReview.addView(reviewAuthor);
                mReview.addView(reviewContent);


            }

        }

    }

}
