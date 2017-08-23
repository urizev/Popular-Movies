package com.urizev.moviesudacity.view.detail;

import com.uwetrottmann.tmdb2.entities.Movie;

class DetailModel {
    final boolean loading;
    final Movie movie;
    final Throwable error;

    private DetailModel(boolean loading, Movie movie, Throwable error) {
        this.loading = loading;
        this.movie = movie;
        this.error = error;
    }

    static DetailModel withMovie(Movie movie) {
        return new DetailModel(false, movie, null);
    }

    DetailModel withError(Throwable throwable) {
        return new DetailModel(loading, movie, throwable);
    }
}
