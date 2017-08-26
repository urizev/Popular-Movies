package com.urizev.moviesudacity.view.common;

public class ErrorViewState implements ViewState {
    public final Throwable error;

    public ErrorViewState(Throwable error) {
        this.error = error;
    }
}
