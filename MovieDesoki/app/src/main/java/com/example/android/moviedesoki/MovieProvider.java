package com.example.android.moviedesoki;

import de.triplet.simpleprovider.AbstractProvider;
import de.triplet.simpleprovider.Column;
import de.triplet.simpleprovider.Table;

public class MovieProvider extends AbstractProvider {

    protected String getAuthority() {
        return "com.example.android.moviedesoki";
    }

    @Table
    public class Favourite {
        @Column(value = Column.FieldType.INTEGER, primaryKey = true)
        public static final String KEY_ID = "_id";

        @Column(value = Column.FieldType.TEXT, since = 2)
        public static final String KEY_TITLE = "title";

        @Column(Column.FieldType.TEXT)
        public static final String KEY_RELEASE = "release_date";

        @Column(Column.FieldType.TEXT)
        public static final String KEY_VOTE = "vote_count";

        @Column(Column.FieldType.TEXT)
        public static final String KEY_RATING = "vote_average";

        @Column(Column.FieldType.TEXT)
        public static final String KEY_REVIEW = "overview";

        @Column(Column.FieldType.BLOB)
        public static final String KEY_POSTER = "poster_path";

    }

    @Override
    protected int getSchemaVersion() {
        return 2;
    }
}

