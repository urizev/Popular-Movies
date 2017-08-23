package com.urizev.moviesudacity.view.detail;

import com.urizev.moviesudacity.view.common.ViewState;

class DetailViewState implements ViewState {
    final String title;
    final String posterPath;
    final int year;
    final int runtime;
    final double voteAverage;
    final String tagline;
    final String overview;
    final Throwable error;

    DetailViewState(String title, String posterPath, int year, Integer runtime, Double voteAverage, String tagline, String overview, Throwable error) {
        this.title = title;
        this.posterPath = posterPath;
        this.year = year;
        this.runtime = runtime != null ? runtime : 0;
        this.voteAverage = voteAverage != null ? voteAverage : 0;
        this.tagline = tagline;
        this.overview = overview;
        this.error = error;
    }
    DetailViewState(String title, String posterPath, int year, Integer runtime, Double voteAverage, String tagline, String overview) {
        this(title, posterPath, year, runtime, voteAverage, tagline, overview, null);
    }
    DetailViewState(Throwable error) {
        this(null, null, 0, null, null, null, null, error);
    }
}
