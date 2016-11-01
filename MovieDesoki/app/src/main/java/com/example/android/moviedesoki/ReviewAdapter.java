package com.example.android.moviedesoki;

import android.app.Activity;
import android.content.Context;
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
public class ReviewAdapter extends ArrayAdapter<MovieData> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<MovieData> moviesList = new ArrayList<>();

    public ReviewAdapter(Context context, int resource, ArrayList<MovieData> moviesList) {
        super(context, resource);
        this.context = context;
        this.layoutResourceId = resource;
        this.moviesList = moviesList;
    }

    public void setReviewData(ArrayList<MovieData> moviesList) {
        this.moviesList = moviesList;
        notifyDataSetChanged();
        Log.v("ReviewAdapter", "hola");

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("ReviewAdapter", "hi1");
        View row = convertView;
        MovieData item;

        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.reviewAuthor = (TextView) row.findViewById(R.id.review_author);
            holder.reviewContent = (TextView) row.findViewById(R.id.review_content);
            row.setTag(holder);
            Log.v("ReviewAdapter", "hi4");
        } else {
            holder = (ViewHolder) row.getTag();
            Log.v("ReviewAdapter", "hi3");
        }
        item = moviesList.get(position);

        Log.v("ReviewAdapter", "Author: " + item.getAuthor());
        Log.v("ReviewAdapter", "Content: " + item.getContent());

        holder.reviewAuthor.setText(item.getAuthor());
        holder.reviewContent.setText(item.getContent());

        Log.v("ReviewAdapter", "hi");
        return row;
    }

    @Override
    public int getCount() {
        Log.v("ReviewAdapter", "Size " + moviesList.size());
        return moviesList.size();
    }

    @Override
    public long getItemId(int position) {
        return moviesList.get(position).hashCode();
    }

    static class ViewHolder {
        TextView reviewAuthor;
        TextView reviewContent;
    }
}
