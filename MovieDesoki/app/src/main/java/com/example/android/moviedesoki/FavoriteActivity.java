package com.example.android.moviedesoki;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class FavoriteActivity extends AppCompatActivity implements FavoriteFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        if (savedInstanceState==null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fav_container , new FavoriteFragment())
                    .commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onItemSelected(Bundle bundle) {
        if (MainActivity.isTwoPane) {
            FavDetailFragment fragment = new FavDetailFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, FavDetailActivity.class)
                    .putExtras(bundle);
            startActivity(intent);
        }
    }
}
