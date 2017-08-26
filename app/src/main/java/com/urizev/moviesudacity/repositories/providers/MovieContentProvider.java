package com.urizev.moviesudacity.repositories.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class MovieContentProvider extends ContentProvider {
    private static final int URI_ROOT = 0;
    private static final int URI_MOVIES = 1;
    private static final int URI_MOVIE_ID = 2;

    private final UriMatcher uriMatcher;
    private MovieDBHelper dbHelper;

    public MovieContentProvider() {
        this.uriMatcher = new UriMatcher(URI_ROOT);
        this.uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.MOVIE_PATH, URI_MOVIES);
        this.uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.MOVIE_PATH + "/#", URI_MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        this.dbHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch(uriMatcher.match(uri)) {
            case URI_MOVIES:
                if (!values.containsKey(MovieContract.FavoriteMovieEntry._ID)) {
                    throw new IllegalArgumentException(MovieContract.FavoriteMovieEntry._ID + " is mandatory");
                }

                String selection = MovieContract.FavoriteMovieEntry._ID + " = ?";
                String [] selectionArgs = new String[]{values.getAsString(MovieContract.FavoriteMovieEntry._ID)};
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor = db.query(MovieContract.FavoriteMovieEntry.TABLE,
                        null,
                        selection,
                        selectionArgs,
                        null, null, null);
                long id;
                if (!cursor.moveToFirst()) {
                    id = db.insert(MovieContract.FavoriteMovieEntry.TABLE, null, values);
                }
                else {
                    id = values.getAsLong(MovieContract.FavoriteMovieEntry._ID);
                    db.update(MovieContract.FavoriteMovieEntry.TABLE, values, selection, selectionArgs);
                }
                cursor.close();
                return MovieContract.FavoriteMovieEntry.CONTENT_URI.buildUpon().appendPath("" + id).build();
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch(uriMatcher.match(uri)) {
            case URI_MOVIES:
                return dbHelper.getReadableDatabase().query(
                        MovieContract.FavoriteMovieEntry.TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case URI_MOVIE_ID:
                String movieId = uri.getLastPathSegment();
                return dbHelper.getReadableDatabase().query(
                        MovieContract.FavoriteMovieEntry.TABLE,
                        projection,
                        MovieContract.FavoriteMovieEntry._ID + " = ?",
                        new String[]{ movieId },
                        null,
                        null,
                        sortOrder);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch(uriMatcher.match(uri)) {
            case URI_MOVIE_ID:
                String movieId = uri.getLastPathSegment();
                return db.delete(MovieContract.FavoriteMovieEntry.TABLE, MovieContract.FavoriteMovieEntry._ID + " = ?", new String[]{ movieId });
            default:
                return db.delete(MovieContract.FavoriteMovieEntry.TABLE, selection, selectionArgs);
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
