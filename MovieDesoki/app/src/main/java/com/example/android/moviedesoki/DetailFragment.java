package com.example.android.moviedesoki;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private ArrayList<MovieData> trailersData = new ArrayList<>();
    private ArrayList<MovieData> reviewsData = new ArrayList<>();
    private Menu Fmenu;
    private Bundle mBundle;
    private ImageView moviePoster;

    public static final Uri CONTENT_URI = Uri.parse("content://com.example.android.moviedesoki/favourites");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mBundle = getArguments() == null ? getActivity().getIntent().getExtras() : getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        setUpLayout(rootView);

        return rootView;
    }

    public void setUpLayout(View rootView) {
        moviePoster = (ImageView) rootView.findViewById(R.id.movie_detail_poster);
        TextView movieOverview = (TextView) rootView.findViewById(R.id.movie_overview);
        TextView movieReleaseDate = (TextView) rootView.findViewById(R.id.movie_realease_date);
        TextView movieVoteAvg = (TextView) rootView.findViewById(R.id.movie_vote_avg);
        TextView movieVoteCount = (TextView) rootView.findViewById(R.id.movie_vote_count);
        LinearLayout isOnlineGroup = (LinearLayout) rootView.findViewById(R.id.is_online_group);

        if (mBundle.getString("PosterFromFav") == null) {
            isOnlineGroup.setVisibility(View.VISIBLE);

            Picasso.with(getActivity()).load(mBundle.getString("PosterFromMov")).into(moviePoster);

            NonScrollListView trailerList = (NonScrollListView) rootView.findViewById(R.id.movie_trailer_list);
            trailersData = new ArrayList<>();
            trailerAdapter = new TrailerAdapter(getActivity(), R.layout.trailer_list_item, trailersData);
            trailerList.setAdapter(trailerAdapter);

            NonScrollListView reviewList = (NonScrollListView) rootView.findViewById(R.id.movie_review_list);
            reviewsData = new ArrayList<>();
            reviewAdapter = new ReviewAdapter(getActivity(), R.layout.review_list_item, reviewsData);
            reviewList.setAdapter(reviewAdapter);

            new FetchTrailer().execute(mBundle.getString("Id"));
            new FetchReview().execute(mBundle.getString("Id"));

            trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    watchYoutubeVideo(trailersData.get(position).getLink());
                }
            });
        } else {
            isOnlineGroup.setVisibility(View.GONE);

            byte[] posterInBytes = mBundle.getByteArray("PosterFromFav");
            Drawable poster = new BitmapDrawable(Resources.getSystem(),
                    BitmapFactory.decodeByteArray(posterInBytes, 0, posterInBytes.length));
            moviePoster.setImageDrawable(poster);
        }

        movieOverview.setText(mBundle.getString("Overview"));
        movieReleaseDate.setText(mBundle.getString("ReleaseDate"));
        movieVoteAvg.setText(mBundle.getString("VoteAvg"));
        movieVoteCount.setText(mBundle.getString("VoteCount"));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        this.Fmenu = menu;

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        MenuItem item2;

        if (id == R.id.favs) {
            item = Fmenu.findItem(R.id.favs2);
            item.setEnabled(true);
            item.setVisible(true);

            item2 = Fmenu.findItem(R.id.favs);
            item2.setEnabled(false);
            item2.setVisible(false);

            AddToFavs();

            Toast.makeText(getContext(), "Added to Favourites", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.favs2) {
            item = Fmenu.findItem(R.id.favs);
            item.setEnabled(true);
            item.setVisible(true);

            item2 = Fmenu.findItem(R.id.favs2);
            item2.setEnabled(false);
            item2.setVisible(false);

            remFavs();

            Toast.makeText(getContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void AddToFavs() {
        ContentValues favMovie = new ContentValues();
        byte[] posterInBytes = toByteArray(drawableToBitmap(moviePoster.getDrawable()));

        favMovie.put(MovieProvider.Favourite.KEY_ID, mBundle.getString("Id"));
        favMovie.put(MovieProvider.Favourite.KEY_RATING, mBundle.getString("VoteAverage"));
        favMovie.put(MovieProvider.Favourite.KEY_VOTE, mBundle.getString("VoteCount"));
        favMovie.put(MovieProvider.Favourite.KEY_REVIEW, mBundle.getString("Overview"));
        favMovie.put(MovieProvider.Favourite.KEY_POSTER, posterInBytes);
        favMovie.put(MovieProvider.Favourite.KEY_RELEASE, mBundle.getString("ReleaseDate"));

        getActivity().getContentResolver().insert(CONTENT_URI, favMovie);
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public void remFavs() {
        getActivity().getContentResolver().delete(CONTENT_URI,
                MovieProvider.Favourite.KEY_ID + " = ?",
                new String[]{mBundle.getString("Id")});
    }

    public void watchYoutubeVideo(String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getContext(),
                DetailFragment.CONTENT_URI,
                new String[]{MovieProvider.Favourite.KEY_ID},
                MovieProvider.Favourite.KEY_ID +
                        " = " +
                        mBundle.getString("Id"),
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        MenuItem item;

        if (!data.moveToFirst()) {
            item = Fmenu.findItem(R.id.favs);
            item.setEnabled(true);
            item.setVisible(true);
        } else {
            item = Fmenu.findItem(R.id.favs2);
            item.setEnabled(true);
            item.setVisible(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class FetchTrailer extends AsyncTask<String, Void, String> {
        MovieData item;

        private final String LOG_TAG = FetchTrailer.class.getSimpleName();

        private void parseMovieData(String movJsonStr) throws JSONException {
            final String OWM_RESULTS = "results";

            final String OWM_LINK = "key";
            final String OWM_NAME = "name";

            JSONObject moviesJSON = new JSONObject(movJsonStr);
            JSONArray movieArray = moviesJSON.getJSONArray(OWM_RESULTS);
            trailersData.clear();

            for (int i = 0; i < movieArray.length(); i++) {
                item = new MovieData();
                JSONObject movie = movieArray.getJSONObject(i);

                item.setLink(movie.getString(OWM_LINK));
                item.setName(movie.getString(OWM_NAME));

                trailersData.add(item);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String MovJsnStr = null;
            HttpURLConnection urlConnection = null;

            BufferedReader reader = null;
            try {
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + params[0] + "/videos"
                                + "?api_key=3b87983e559703a6a09f2ed7ce565ec0";
                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon().build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.v("FetchMovie", "Nothing to do");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                MovJsnStr = buffer.toString();
                Log.v("FetchMovie, Trailer", MovJsnStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                parseMovieData(MovJsnStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return MovJsnStr;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                trailerAdapter.clear();
                trailerAdapter.setTrailerData(trailersData);
            } else {
                Toast.makeText(getActivity(), "No trailers found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class FetchReview extends AsyncTask<String, Void, String> {
        MovieData item;
        private ArrayList<MovieData> moviesList = new ArrayList<>();

        private final String LOG_TAG = FetchReview.class.getSimpleName();

        private void parseMovieData(String movJsonStr) throws JSONException {
            final String OWM_RESULTS = "results";

            final String OWM_CONTENT = "content";
            final String OWM_AUTHOR = "author";

            JSONObject moviesJSON = new JSONObject(movJsonStr);
            JSONArray movieArray = moviesJSON.getJSONArray(OWM_RESULTS);

            moviesList.clear();

            for (int i = 0; i < movieArray.length(); i++) {
                item = new MovieData();
                JSONObject movie = movieArray.getJSONObject(i);

                item.setContent(movie.getString(OWM_CONTENT));
                item.setAuthor(movie.getString(OWM_AUTHOR));

                moviesList.add(item);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String MovJsnStr = null;
            HttpURLConnection urlConnection = null;

            BufferedReader reader = null;
            try {
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews"
                                + "?api_key=3b87983e559703a6a09f2ed7ce565ec0";
                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon().build();
                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.v("FetchMovie", "Nothing to do");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                MovJsnStr = buffer.toString();
                Log.v("FetchMovie, Reviews", MovJsnStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                parseMovieData(MovJsnStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return MovJsnStr;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                reviewAdapter.clear();
                reviewAdapter.setReviewData(moviesList);
            } else {
                Toast.makeText(getActivity(), "No reviews found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

