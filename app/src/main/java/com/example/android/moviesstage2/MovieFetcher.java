package com.example.android.moviesstage2;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieFetcher {

    private static final String TAG = "MovieFetcher";
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String MOVIE_IMAGE_PATH = "http://image.tmdb.org/t/p/w185/";
    private static final String POPULAR_QUERY_STRING = "popular";
    private static final String API_KEY = BuildConfig.API_KEY;

    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() + ":with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    public List<MovieItem> fetchItems(){
        List<MovieItem> items = new ArrayList<>();
        try {
            String url = Uri.parse(MOVIE_BASE_URL)
                    .buildUpon()
                    .appendPath(POPULAR_QUERY_STRING)
                    .appendQueryParameter("api_key", API_KEY)
                    .build()
                    .toString();

            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (IOException ioe){
            Log.e(TAG, "Failed to fetch items", ioe);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return items;
    }

    private void parseItems(List<MovieItem> items, JSONObject jsonBody)
            throws IOException, JSONException{

        JSONArray photoJsonArray = jsonBody.getJSONArray("results");

        for (int i = 0; i < photoJsonArray.length(); i++){
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            MovieItem item = new MovieItem();

            item.setMovieId(photoJsonObject.getString("id"));
            item.setTitle(photoJsonObject.getString("title"));

            item.setPosterPath(MOVIE_IMAGE_PATH + photoJsonObject.getString("poster_path"));
            items.add(item);

        }

    }
}
