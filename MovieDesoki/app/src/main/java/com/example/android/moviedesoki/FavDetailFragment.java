package com.example.android.moviedesoki;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by islam eldesoky on 24/09/2016.
 */
public class FavDetailFragment extends Fragment {
    private Menu mMenu;
    private Bundle mBundle;

    TextView movieOverview;
    TextView movieReleaseDate;
    TextView movieVoteAvg;
    TextView movieVoteCount;
    ImageView moviePoster;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mBundle = getArguments() == null ? getActivity().getIntent().getExtras() : getArguments();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fav_detail_fragment, container, false);

        moviePoster = (ImageView) rootView.findViewById(R.id.fav_movie_detail_poster);
        byte[] posterInBytes = mBundle.getByteArray("PosterFromFav");
        Drawable poster = new BitmapDrawable(Resources.getSystem(),
                BitmapFactory.decodeByteArray(posterInBytes, 0, posterInBytes.length));
        moviePoster.setImageDrawable(poster);

        movieOverview = (TextView) rootView.findViewById(R.id.fav_movie_overview);
        movieOverview.setText(mBundle.getString("Overview"));

        movieReleaseDate = (TextView) rootView.findViewById(R.id.fav_movie_realease_date);
        movieReleaseDate.setText(mBundle.getString("ReleaseDate"));

        movieVoteAvg = (TextView) rootView.findViewById(R.id.fav_movie_vote_avg);
        movieVoteAvg.setText(mBundle.getString("VoteAvg"));

        movieVoteCount = (TextView) rootView.findViewById(R.id.fav_movie_vote_count);
        movieVoteCount.setText(mBundle.getString("VoteCount"));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        this.mMenu = menu;
        MenuItem whiteFavIcon = menu.findItem(R.id.favs2);
        MenuItem borderFavIcon = menu.findItem(R.id.favs);

        whiteFavIcon.setVisible(true);
        whiteFavIcon.setEnabled(true);

        borderFavIcon.setVisible(false);
        borderFavIcon.setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuItem notSelectedFavIcon;

        switch (item.getItemId()) {
            case R.id.favs2:
                item = mMenu.findItem(R.id.favs);
                item.setEnabled(true);
                item.setVisible(true);

                notSelectedFavIcon = mMenu.findItem(R.id.favs2);
                notSelectedFavIcon.setEnabled(false);
                notSelectedFavIcon.setVisible(false);

                removeFavourite();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void removeFavourite() {
        getActivity().getContentResolver().delete(DetailFragment.CONTENT_URI,
                MovieProvider.Favourite.KEY_ID + " = ?",
                new String[]{mBundle.getString("Id")});
    }
}

