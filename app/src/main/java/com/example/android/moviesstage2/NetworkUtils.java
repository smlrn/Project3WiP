package com.example.android.moviesstage2;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String MOVIE_IMAGE_PATH = "http://image.tmdb.org/t/p/w185/";
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/";
    private static final String API_VERSION = "3";
    private static final String MOVIE = "movie";
    private static final String VIDEOS = "videos";
    private static final String REVIEWS = "reviews";
    private static final String API_KEY_PARAM = "api_key";


    private NetworkUtils(){}

    public static List<MovieItem> extractMovies(String urlString) {

        URL url = createUrl(urlString);
        String jsonResponse = null;

        try {
            jsonResponse = getMovieInfo(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making HTTP request", e);
        }

        return extractFeatureFromJSON(jsonResponse);
    }

    public static URL createUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Unable to create URL", e);
        }

        return url;
    }


    public static String getMovieInfo(URL url) throws IOException{


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJSONString;


        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) return null;

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) return null;

            movieJSONString = buffer.toString();


        }catch (IOException e){
            e.printStackTrace();
            return null;

        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

        }
        Log.i(LOG_TAG, movieJSONString);
        return movieJSONString;


    }


    public  static List<MovieItem> extractFeatureFromJSON(String urlString){

        List<MovieItem> movies = new ArrayList<>();
        Log.i(LOG_TAG, urlString);
        try {
            JSONObject jsonObject = new JSONObject(urlString);
            JSONArray itemsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i<itemsArray.length(); i++){
                JSONObject movie = itemsArray.getJSONObject(i);

                String id = String.valueOf(movie.getInt("id"));
                String title = movie.getString("original_title");
                String poster = MOVIE_IMAGE_PATH + movie.getString("poster_path");
                String synopsis = movie.getString("overview");
                double rating = movie.getDouble("vote_average");
                String releaseDate = movie.getString("release_date");

                movies.add(new MovieItem(id, title, poster, synopsis, rating, releaseDate));

            }
        } catch (JSONException e){
            Log.e(LOG_TAG, "Parse error", e);
        }

        return movies;

    }



    public static URL buildTrailerUrl(String movieId){
        Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(API_VERSION)
                .appendPath(MOVIE)
                .appendPath(movieId)
                .appendPath(VIDEOS)
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildReviewsUrl(String movieId){
        Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(API_VERSION)
                .appendPath(MOVIE)
                .appendPath(movieId)
                .appendPath(REVIEWS)
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;

    }

}
