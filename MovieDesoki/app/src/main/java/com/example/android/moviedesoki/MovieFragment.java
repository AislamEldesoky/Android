package com.example.android.moviedesoki;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MovieFragment extends Fragment {
    private ArrayList<MovieData> moviesList = new ArrayList<>();
    private GridAdapter moviesAdapter;
    private GridView moviesGrid;

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private boolean flag;

    public interface Callback {
        void onItemSelected(Bundle bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        moviesGrid = (GridView) rootView.findViewById(R.id.grid_view);
        moviesList = new ArrayList<>();
        moviesAdapter = new GridAdapter(getActivity(),
                R.layout.grid_item,
                moviesList);
        moviesGrid.setAdapter(moviesAdapter);

        mPlanetTitles = getResources().getStringArray(R.array.NavItems);
        mDrawerLayout = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) rootView.findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.drawer_list_item, mPlanetTitles));

        // Set the list's click listener
        if (!flag) {
            new FetchMovie().execute("top_rated");
            flag = true;
        }

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (flag) {
                        new FetchMovie().execute("top_rated");
                    }
                    Toast.makeText(getActivity(), "Top Rated clicked", Toast.LENGTH_SHORT).show();
                } else {
                    new FetchMovie().execute("popular");
                    Toast.makeText(getActivity(), "Popular clicked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.v("Fragment", "Done fetching");

        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("Id", moviesList.get(position).getId());
                bundle.putString("Title", moviesList.get(position).getTitle());
                bundle.putString("PosterFromMov", moviesList.get(position).getPoster());
                bundle.putString("Overview", moviesList.get(position).getReview());
                bundle.putString("ReleaseDate",  moviesList.get(position).getReleaseDate());
                bundle.putString("VoteAvg", moviesList.get(position).getRating());
                bundle.putString("VoteCount", moviesList.get(position).getVoteCount());

                ((Callback) getActivity()).onItemSelected(bundle);
            }
        });

        return rootView;
    }

    public class FetchMovie extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = FetchMovie.class.getSimpleName();

        private void parseMovieData(String movJsonStr) throws JSONException {
            final String OWM_RESULTS = "results";
            final String OWM_TITLE = "original_title";
            final String OWM_POSTER = "poster_path";
            final String OWM_RELEASE_DATE = "release_date";
            final String OWM_SYNOPSIS = "overview";
            final String OWM_RATING = "vote_average";
            final String OWM_VOTECOUNT = "vote_count";
            final String OWM_ID = "id";

            JSONObject moviesJSON = new JSONObject(movJsonStr);
            JSONArray movieArray = moviesJSON.getJSONArray(OWM_RESULTS);

            MovieData item;

            moviesList.clear();

            for (int i = 0; i < movieArray.length(); i++) {
                item = new MovieData();
                JSONObject movie = movieArray.getJSONObject(i);

                item.setId(movie.getString(OWM_ID));
                item.setPoster("http://image.tmdb.org/t/p/w185" + movie.getString(OWM_POSTER));
                item.setRating(movie.getString(OWM_RATING));
                item.setReview(movie.getString(OWM_SYNOPSIS));
                item.setTitle(movie.getString(OWM_TITLE));
                item.setReleaseDate(movie.getString(OWM_RELEASE_DATE));
                item.setVoteCount(movie.getString(OWM_VOTECOUNT));
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
                        "http://api.themoviedb.org/3/movie/"
                                + params[0]
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
                Log.v("FetchMovie", MovJsnStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
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
                moviesAdapter.clear();
                moviesAdapter.setGridData(moviesList);
                Log.v(LOG_TAG, "hi5");
            } else {
                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
