package com.example.android.moviedesoki;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridAdapter extends ArrayAdapter<MovieData> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<MovieData> moviesList = new ArrayList<>();

    public GridAdapter(Context context, int resource, ArrayList<MovieData> moviesList) {
        super(context, resource);
        this.context = context;
        this.layoutResourceId = resource;
        this.moviesList = moviesList;
    }
// copy everything w 27na 7anshel henak oh ok
    public void setGridData(ArrayList<MovieData> moviesList) {
        this.moviesList = moviesList;
        notifyDataSetChanged();
        Log.v("GridAdapter", "hola");

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("GridAdapter", "hi1");
        View row = convertView;
        MovieData item ;

        ViewHolder holder;

        if (row==null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.moviePoster = (ImageView) row.findViewById(R.id.movie_poster); // fix the pic and rebuild the project and everything will be fin
            row.setTag(holder);
            Log.v("GridAdapter", "hi4");
        } else {
            holder = (ViewHolder) row.getTag();
            Log.v("GridAdapter", "hi3");

        }
        item = moviesList.get(position);
        Picasso.with(context).load(item.getPoster()).into(holder.moviePoster);

        Log.v("GridAdapter", "hi");
        return row;
    }
   @Override
    public int getCount(){
       Log.v("GridAdapter", "Size " + moviesList.size());
       return moviesList.size();
   }
    @Override
    public long getItemId(int position)
    {
        return moviesList.get(position).hashCode();
    }

    static class ViewHolder {
        ImageView moviePoster;
    }
}
