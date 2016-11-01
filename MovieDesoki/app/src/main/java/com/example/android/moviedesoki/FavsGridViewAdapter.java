package com.example.android.moviedesoki;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FavsGridViewAdapter extends CursorAdapter {

    public FavsGridViewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        /**
         * Get image in bytes and convert it to a drawable
         */
       byte[] posterInBytes = cursor.getBlob(FavoriteFragment.COLUMN_MOVIE_POSTER);
        Drawable poster = new BitmapDrawable(Resources.getSystem(),
                BitmapFactory.decodeByteArray(posterInBytes, 0, posterInBytes.length));

        ImageView posterView = (ImageView) view.findViewById(R.id.movie_poster);
        posterView.setImageDrawable(poster);


    }
}
