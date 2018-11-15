package com.example.android.moviesstage2;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PopularMoviesActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return MovieListFragment.newInstance();
    }


}
