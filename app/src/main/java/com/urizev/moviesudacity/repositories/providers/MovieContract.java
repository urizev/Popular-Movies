package com.urizev.moviesudacity.repositories.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MovieContract {
    static final String AUTHORITY = "com.urizev.moviesudacity";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    static final String MOVIE_PATH = "movies";

    public final static class FavoriteMovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIE_PATH).build();

        static final String TABLE = "movies";

        public static final String POSTER_PATH = "poster_path";
        public static final String TITLE = "title";
    }
}
