package com.urizev.moviesudacity.view.common;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public abstract class Presenter<VS extends ViewState> {
    private final BehaviorSubject<VS> viewStateSubject = BehaviorSubject.create();
    private final CompositeDisposable disposables;

    protected Presenter() {
        disposables = new CompositeDisposable();
    }

    Observable<VS> viewStateObservable() {
        return viewStateSubject;
    }

    protected void publishViewState(VS viewState) {
        viewStateSubject.onNext(viewState);
    }

    protected void addDisposable(Disposable loadMovieDisposable) {
        disposables.add(loadMovieDisposable);
    }

    void dispose() {
        disposables.dispose();
    }
}
