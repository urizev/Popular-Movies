package com.urizev.moviesudacity.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.urizev.moviesudacity.repositories.cache.MovieCache;
import com.urizev.moviesudacity.repositories.providers.MovieContract;
import com.urizev.moviesudacity.repositories.services.MovieService;
import com.uwetrottmann.tmdb2.entities.BaseMovie;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class MovieRepository {
    public static final int POPULAR = 0;
    public static final int TOP_RATED = 1;
    public static final int FAVORITES = 2;

    private final Context context;
    private final MovieCache movieCache;
    private final MovieService movieService;

    public MovieRepository(Context context, MovieCache movieCache, MovieService movieService) {
        this.context = context;
        this.movieCache = movieCache;
        this.movieService = movieService;
    }

    public Observable<List<BaseMovie>> list(final int listType, final int listPage) {
        if (listType == FAVORITES) {
            return favoriteList(listPage);
        }

        Observable<MovieResultsPage> serviceObservable = movieService.list(listType, listPage);

        return movieCache.getList(listType, listPage)
                .toObservable()
                .switchIfEmpty(serviceObservable)
                .doOnNext(new Consumer<MovieResultsPage>() {
                    @Override
                    public void accept(MovieResultsPage results) throws Exception {
                        movieCache.putList(listType, listPage, results);
                    }
                })
                .map(new Function<MovieResultsPage, List<BaseMovie>>() {
                    @Override
                    public List<BaseMovie> apply(@NonNull MovieResultsPage movieResultsPage) throws Exception {
                        return Observable.fromIterable(movieResultsPage.results)
                                .toList()
                                .blockingGet();
                    }
                });
    }

    private Observable<List<BaseMovie>> favoriteList(int listPage) {
        if (listPage != 1) {
            return Observable.just(Collections.<BaseMovie>emptyList());
        }

        return Observable.fromCallable(new Callable<List<BaseMovie>>() {
            @Override
            public List<BaseMovie> call() throws Exception {
                ContentResolver contentResolver = context.getContentResolver();
                Cursor cursor = contentResolver.query(MovieContract.FavoriteMovieEntry.CONTENT_URI, null, null, null, null);
                List<BaseMovie> movies;
                if (cursor != null && cursor.moveToFirst()) {
                    movies = new ArrayList<>(cursor.getCount());
                    int idIndex = cursor.getColumnIndex(MovieContract.FavoriteMovieEntry._ID);
                    int titleIndex = cursor.getColumnIndex(MovieContract.FavoriteMovieEntry.TITLE);
                    int posterPathIndex = cursor.getColumnIndex(MovieContract.FavoriteMovieEntry.POSTER_PATH);
                    while (!cursor.isAfterLast()) {
                        BaseMovie movie = new BaseMovie();
                        movie.id = cursor.getInt(idIndex);
                        movie.original_title = cursor.getString(titleIndex);
                        movie.poster_path = cursor.getString(posterPathIndex);
                        movies.add(movie);
                        cursor.moveToNext();
                    }
                }
                else {
                    movies = Collections.emptyList();
                }

                if (cursor != null) {
                    cursor.close();
                }

                return movies;
            }
        });
    }

    public Observable<Movie> detail(int movieId) {
        Observable<Movie> serviceObservable = movieService.detail(movieId)
                .doOnNext(new Consumer<Movie>() {
                    @Override
                    public void accept(Movie movie) throws Exception {
                        movieCache.putDetail(movie);
                    }
                });

        return movieCache.getDetail(movieId)
                .toObservable()
                .filter(new Predicate<Movie>() {
                    @Override
                    public boolean test(@NonNull Movie movie) throws Exception {
                        return movie.runtime != null;
                    }
                })
                .switchIfEmpty(serviceObservable)
                .concatWith(serviceObservable);
    }

    public Completable markAsFavorite(final Movie movie, final boolean favorite) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                String strMovieId = String.valueOf(movie.id);
                if (favorite) {
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.FavoriteMovieEntry._ID, movie.id);
                    values.put(MovieContract.FavoriteMovieEntry.TITLE, movie.original_title);
                    values.put(MovieContract.FavoriteMovieEntry.POSTER_PATH, movie.poster_path);
                    context.getContentResolver().insert(MovieContract.FavoriteMovieEntry.CONTENT_URI, values);
                }
                else {
                    Uri uri = MovieContract.FavoriteMovieEntry.CONTENT_URI
                            .buildUpon()
                            .appendPath(strMovieId)
                            .build();
                    context.getContentResolver().delete(uri, MovieContract.FavoriteMovieEntry._ID + "= ?", new String[] { strMovieId });
                }
            }
        });
    }

    public Single<Boolean> isFavorite(final int movieId) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String strMovieId = String.valueOf(movieId);
                Uri uri = MovieContract.FavoriteMovieEntry.CONTENT_URI
                        .buildUpon()
                        .appendPath(strMovieId)
                        .build();


                Cursor cursor = context.getContentResolver().query(uri, null, MovieContract.FavoriteMovieEntry._ID + "= ?", new String[]{strMovieId}, null);
                boolean isFavorite = cursor != null && cursor.moveToFirst();
                if (cursor != null) {
                    cursor.close();
                }

                return isFavorite;
            }
        });
    }
}
