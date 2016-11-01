package com.example.android.moviedesoki;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


public class FavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int FAVOURITE_LOADER = 0;
    private FavsGridViewAdapter mFavsGridViewAdapter;

    private String[] COLUMNS = {
            MovieProvider.Favourite.KEY_ID,
            MovieProvider.Favourite.KEY_POSTER,
            MovieProvider.Favourite.KEY_TITLE,
            MovieProvider.Favourite.KEY_REVIEW,
            MovieProvider.Favourite.KEY_RELEASE,
            MovieProvider.Favourite.KEY_RATING,
            MovieProvider.Favourite.KEY_VOTE
    };

    public static final int COLUMN_MOVIE_ID = 0;
    public static final int COLUMN_MOVIE_POSTER = 1;
    public static final int COLUMN_MOVIE_TITLE = 2;
    public static final int COLUMN_MOVIE_OVERVIEW = 3;
    public static final int COLUMN_MOVIE_RELEASE_DATE = 4;
    public static final int COLUMN_MOVIE_VOTE_AVG = 5;
    public static final int COLUMN_MOVIE_VOTE_COUNT = 6;

    public interface Callback {
        void onItemSelected(Bundle bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        getLoaderManager().initLoader(FAVOURITE_LOADER, null, this);

        mFavsGridViewAdapter = new FavsGridViewAdapter(getContext(), null, 0);
        GridView gridView = (GridView) rootView.findViewById(R.id.favs_grid);
        gridView.setAdapter(mFavsGridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("Id", cursor.getString(COLUMN_MOVIE_ID));
                    bundle.putString("Title", cursor.getString(COLUMN_MOVIE_TITLE));
                    bundle.putByteArray("PosterFromFav", cursor.getBlob(COLUMN_MOVIE_POSTER));
                    bundle.putString("Overview", cursor.getString(COLUMN_MOVIE_OVERVIEW));
                    bundle.putString("ReleaseDate", cursor.getString(COLUMN_MOVIE_RELEASE_DATE));
                    bundle.putString("VoteAvg", cursor.getString(COLUMN_MOVIE_VOTE_AVG));
                    bundle.putString("VoteCount", cursor.getString(COLUMN_MOVIE_VOTE_COUNT));

                    ((Callback) getActivity()).onItemSelected(bundle);
                }
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getContext(),
                DetailFragment.CONTENT_URI,
                COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            Toast.makeText(getContext(), "No Movies added to Favourites yet", Toast.LENGTH_LONG).show();
        } else {
            mFavsGridViewAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavsGridViewAdapter.swapCursor(null);
    }
}
