package com.urizev.moviesudacity.repositories.cache;

import android.support.v4.util.ArrayMap;
import android.util.SparseArray;

import com.uwetrottmann.tmdb2.entities.BaseMovie;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;

import java.util.Locale;

import io.reactivex.Maybe;

public class MovieCache {
    private final ArrayMap<String,MovieResultsPage> listCache = new ArrayMap<>();
    private final SparseArray<Movie> movieCache = new SparseArray<>();

    public void putList(int listType, int listPage, MovieResultsPage value) {
        for (BaseMovie baseMovie : value.results) {
            synchronized (movieCache) {
                Movie movie = movieCache.get(baseMovie.id);
                if (movie == null) {
                    movie = new Movie();
                    movieCache.put(baseMovie.id, movie);
                }
                movie.id = baseMovie.id;
                movie.title = baseMovie.title;
                movie.poster_path = baseMovie.poster_path;
                movie.backdrop_path = baseMovie.backdrop_path;
                movie.overview = baseMovie.overview;
                movie.release_date = baseMovie.release_date;
            }
        }
        synchronized (listCache) {
            listCache.put(keyForList(listType, listPage), value);
        }
    }

    public Maybe<MovieResultsPage> getList(int popular, int page) {
        synchronized (listCache) {
            MovieResultsPage value = listCache.get(MovieCache.keyForList(popular, page));
            return value == null ? Maybe.<MovieResultsPage>empty() : Maybe.just(value);
        }
    }

    private static String keyForList(int popular, int page) {
        return String.format(Locale.US, "%d_%d", popular, page);
    }

    public Maybe<Movie> getDetail(int movieId) {
        synchronized (movieCache) {
            Movie movie = movieCache.get(movieId);
            return movie == null ? Maybe.<Movie>empty() : Maybe.just(movie);
        }
    }

    public void putDetail(Movie movie) {
        synchronized (movieCache) {
            movieCache.put(movie.id, movie);
        }
    }
}
