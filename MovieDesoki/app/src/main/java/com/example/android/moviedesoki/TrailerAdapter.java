package com.example.android.moviedesoki;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by islam eldesoky on 21/09/2016.
 */
public class TrailerAdapter extends ArrayAdapter<MovieData> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<MovieData> moviesList = new ArrayList<>();

    public TrailerAdapter(Context context, int resource, ArrayList<MovieData> moviesList) {
        super(context, resource);
        this.context = context;
        this.layoutResourceId = resource;
        this.moviesList = moviesList;
    }

    public void setTrailerData(ArrayList<MovieData> moviesList) {
        this.moviesList = moviesList;
        notifyDataSetChanged();
        Log.v("TrailerAdapter", "hola");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Log.v("TrailerAdapter", "hi1");
        View row = convertView;
        MovieData item;

        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.videoName = (TextView) row.findViewById(R.id.movie_trailer_name);
            row.setTag(holder);
            Log.v("TrailerAdapter", "hi4");
        } else {
            holder = (ViewHolder) row.getTag();
            Log.v("TrailerAdapter", "hi3");
        }
        item = moviesList.get(position);
        holder.videoName.setText(item.getName()); //dude m7tnash holder lel link !wla eh.. yes lakn we won't need it this way umm

        Log.v("TrailerAdapter", "hi");
        return row;
    }

    @Override
    public int getCount() {
        Log.v("TrailerAdapter", "Size " + moviesList.size());
        return moviesList.size();
    }

    @Override
    public long getItemId(int position) {
        return moviesList.get(position).hashCode();
    }

    static class ViewHolder {
        TextView videoName;
    }
}
