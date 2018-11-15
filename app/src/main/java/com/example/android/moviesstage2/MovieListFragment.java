package com.example.android.moviesstage2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieListFragment extends Fragment {

    private static final String TAG = "MovieListFragment";
    private static final String MOVIE_KEY = "MOVIE_KEY";
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY = BuildConfig.API_KEY;

    private RecyclerView mMovieListRecyclerView;
    private List<MovieItem> mItems = new ArrayList<>();
    private MoviePosterDownloader<MovieHolder> mMoviePosterDownloader;

    public static MovieListFragment newInstance(){
        return new MovieListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchMoviesTask().execute();

        Handler responseHandler = new Handler();

        mMoviePosterDownloader = new MoviePosterDownloader<>(responseHandler);
        mMoviePosterDownloader
                .setMoviePosterDownloadListener(new MoviePosterDownloader
                        .MoviePosterDownloadListener<MovieHolder>() {
            @Override
            public void onMoviePosterDownloaded(MovieHolder target, Bitmap poster) {
                Drawable drawable = new BitmapDrawable(getResources(), poster);
                target.bindDrawable(drawable);

            }
        });
        mMoviePosterDownloader.start();
        mMoviePosterDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        mMovieListRecyclerView = v.findViewById(R.id.movie_list_recycler_view);
        mMovieListRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        setupAdapter();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMoviePosterDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMoviePosterDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter() {
        if (isAdded()){
            mMovieListRecyclerView.setAdapter(new MovieAdapter(mItems));
        }
    }

    private class MovieHolder extends RecyclerView.ViewHolder {
        private ImageView mPosterImageView;

        public MovieHolder(View itemView) {
            super(itemView);

            mPosterImageView = (ImageView) itemView.findViewById(R.id.movie_image_view);
        }

        public void bindDrawable(Drawable drawable){
            mPosterImageView.setImageDrawable(drawable);
            // set onclick listener
            mPosterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent movieDetailIntent = new Intent(MovieListFragment.this.getActivity(),
                            MoviesDetailActivity.class);

                    startActivity(movieDetailIntent);
                }
            });
        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {
        private List<MovieItem> mMovieItems;

        public MovieAdapter(List<MovieItem> movieItems){
            mMovieItems = movieItems;
        }


        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.movie_item, parent, false);
            return new MovieHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
            MovieItem movieItem = mMovieItems.get(position);

            Drawable placeholder = getResources().getDrawable(R.drawable.ic_blur_on_black_24dp);
            holder.bindDrawable(placeholder);

            mMoviePosterDownloader.queueMoviePoster(holder, movieItem.getPosterPath());

        }

        @Override
        public int getItemCount() {
            return mMovieItems.size();
        }
    }

    private class FetchMoviesTask extends AsyncTask<Void,Void,List<MovieItem>>{

        @Override
        protected List<MovieItem> doInBackground(Void... voids) {
            return new MovieFetcher().fetchItems();
        }

        @Override
        protected void onPostExecute(List<MovieItem> movieItems) {
            mItems=movieItems;
            setupAdapter();
        }
    }




}
