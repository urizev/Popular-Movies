package com.urizev.moviesudacity.view.main;

import com.urizev.moviesudacity.view.common.ViewState;

class MovieViewState implements ViewState {
    public final int id;
    public final String poster;

    public MovieViewState(int id, String poster) {
        this.id = id;
        this.poster = poster;
    }
}
