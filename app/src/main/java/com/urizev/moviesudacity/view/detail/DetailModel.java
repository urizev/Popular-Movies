package com.urizev.moviesudacity.view.detail;

import com.uwetrottmann.tmdb2.entities.Movie;

class DetailModel {
    private final boolean loading;
    final Movie movie;
    final Throwable error;
    final boolean favorite;

    private DetailModel(boolean loading, Movie movie, boolean favorite, Throwable error) {
        this.loading = loading;
        this.movie = movie;
        this.error = error;
        this.favorite = favorite;
    }

    static DetailModel withMovie(Movie movie, boolean favorite) {
        return new DetailModel(false, movie, favorite, null);
    }

    DetailModel withError(Throwable throwable, boolean favorite) {
        return new DetailModel(loading, movie, favorite, throwable);
    }

    DetailModel withFavorite(boolean favorite) {
        return new DetailModel(loading, movie, favorite, error);
    }
}
