package com.example.android.moviesstage2;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieItem implements Parcelable {
    private String mMovieId;
    private String mTitle;
    private String mPosterPath;
    private String mSynopsis;
    private double mRating;
    private String mReleaseDate;

    protected MovieItem(Parcel in) {
        mMovieId = in.readString();
        mTitle = in.readString();
        mPosterPath = in.readString();
        mSynopsis = in.readString();
        mRating = in.readDouble();
        mReleaseDate = in.readString();
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    @Override
    public String toString() {
        return mTitle;
    }

    public MovieItem(){}

    public MovieItem(String id, String movieTitle, String moviePoster, String movieSynopsis,
                 double movieRating, String movieReleaseDate){
        this.mMovieId = id;
        this.mTitle = movieTitle;
        this.mPosterPath = moviePoster;
        this.mSynopsis = movieSynopsis;
        this.mRating = movieRating;
        this.mReleaseDate = movieReleaseDate;


    }

    public String getMovieId() {
        return mMovieId;
    }

    public void setMovieId(String movieId) {
        mMovieId = movieId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public void setSynopsis(String synopsis) {
        mSynopsis = synopsis;
    }

    public double getRating() {
        return mRating;
    }

    public void setRating(double rating) {
        mRating = rating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieId);
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mSynopsis);
        dest.writeString(mReleaseDate);
        dest.writeDouble(mRating);

    }
}
