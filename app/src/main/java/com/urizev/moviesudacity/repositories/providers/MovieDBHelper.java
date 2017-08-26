package com.urizev.moviesudacity.repositories.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class MovieDBHelper extends SQLiteOpenHelper {
    private static final String NAME = "movies";
    private static final int VERSION = 1;
    private static final String CREATE_MOVIES_SQL = "CREATE TABLE " + MovieContract.FavoriteMovieEntry.TABLE + "(" +
            MovieContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY," +
            MovieContract.FavoriteMovieEntry.TITLE + " TEXT," +
            MovieContract.FavoriteMovieEntry.POSTER_PATH + " TEXT)";

    MovieDBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MOVIES_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
