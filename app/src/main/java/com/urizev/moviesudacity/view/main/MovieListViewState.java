package com.urizev.moviesudacity.view.main;

import com.urizev.moviesudacity.view.common.ViewState;

import java.util.List;

class MovieListViewState implements ViewState {
    final Throwable throwable;
    final List<MovieViewState> listViewState;
    final boolean loading;
    final String title;

    MovieListViewState(String title, List<MovieViewState> movieViewStates, boolean loading, Throwable throwable) {
        this.listViewState = movieViewStates;
        this.throwable = throwable;
        this.loading = loading;
        this.title = title;
    }
}
