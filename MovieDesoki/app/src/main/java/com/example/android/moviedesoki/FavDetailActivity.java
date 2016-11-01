package com.example.android.moviedesoki;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by islam eldesoky on 24/09/2016.
 */
public class FavDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav_detail_activity);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("Title"));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fav_movie_container, new FavDetailFragment())
                    .commit();
        }
    }
}
