package com.urizev.moviesudacity.view.main;

import com.urizev.moviesudacity.providers.SettingsProvider;
import com.urizev.moviesudacity.repositories.MovieRepository;
import com.urizev.moviesudacity.view.common.Presenter;
import com.uwetrottmann.tmdb2.entities.BaseMovie;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.schedulers.Schedulers;

import static com.urizev.moviesudacity.repositories.MovieRepository.POPULAR;

class MovieListPresenter extends Presenter<MovieListViewState> {
    private final MovieRepository movieRepository;
    private final BehaviorProcessor<MovieListModel> model;
    private Disposable loadMovieDisposable;

    MovieListPresenter(MovieRepository movieRepository, SettingsProvider settingsProvider) {
        int listType = settingsProvider.getListType();
        this.model = BehaviorProcessor.createDefault(MovieListModel.newModelWidthListType(listType));
        this.movieRepository = movieRepository;
        addDisposable(settingsProvider.observeEvents()
                .filter(new Predicate<SettingsProvider.SettingsChangedEvent>() {
                    @Override
                    public boolean test(@NonNull SettingsProvider.SettingsChangedEvent event) throws Exception {
                        return event.key.equals(SettingsProvider.KEY_LIST_ORDER);
                    }
                }).map(new Function<SettingsProvider.SettingsChangedEvent, MovieListModel>() {
                    @Override
                    public MovieListModel apply(@NonNull SettingsProvider.SettingsChangedEvent event) throws Exception {
                        int listOrder = event.sharedPreferences.getInt(event.key, POPULAR);
                        return MovieListModel.newModelWidthListType(listOrder);
                    }
                 }).doOnNext(new Consumer<MovieListModel>() {
                    @Override
                    public void accept(MovieListModel newModel) throws Exception {
                        model.onNext(newModel);
                        loadMovies();
                    }
                }).subscribe()
        );

        addDisposable(model
                .map(new Function<MovieListModel, MovieListViewState>() {
                    @Override
                    public MovieListViewState apply(@NonNull final MovieListModel model) throws Exception {
                        return Observable.fromIterable(model.movies)
                                .map(new Function<BaseMovie, MovieViewState>() {
                                    @Override
                                    public MovieViewState apply(@NonNull BaseMovie baseMovie) throws Exception {
                                        return new MovieViewState(baseMovie.id, baseMovie.poster_path);
                                    }
                                })
                                .toList()
                                .map(new Function<List<MovieViewState>, MovieListViewState>() {
                                    @Override
                                    public MovieListViewState apply(@NonNull List<MovieViewState> movieViewStates) throws Exception {
                                        return new MovieListViewState(movieViewStates, model.loading, model.error);
                                    }
                                }).blockingGet();
                    }
                })
                .doOnNext(new Consumer<MovieListViewState>() {
                    @Override
                    public void accept(MovieListViewState viewState) throws Exception {
                        publishViewState(viewState);
                    }
                })
                .subscribe());
        loadMovies();
    }

    void loadMoreMovies() {
        if (!loadMovieDisposable.isDisposed()) {
            return;
        }

        MovieListModel currentModel = model.getValue();
        model.onNext(currentModel.nextPageModel());
        loadMovies();
    }

    private void loadMovies() {
        if (loadMovieDisposable != null && !loadMovieDisposable.isDisposed()) {
            loadMovieDisposable.dispose();
        }
        loadMovieDisposable = movieRepository.list(model.getValue().listType, model.getValue().listPage)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .map(new Function<List<BaseMovie>, MovieListModel>() {
                    @Override
                    public MovieListModel apply(@NonNull List<BaseMovie> movies) throws Exception {
                        MovieListModel currentModel = model.getValue();
                        return currentModel.withAppendedMovies(movies);
                    }
                })
                .onErrorReturn(new Function<Throwable, MovieListModel>() {
                    @Override
                    public MovieListModel apply(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        MovieListModel currentModel = model.getValue();
                        return currentModel.withError(throwable);
                    }
                })
                .doOnNext(new Consumer<MovieListModel>() {
                    @Override
                    public void accept(MovieListModel newModel) throws Exception {
                        model.onNext(newModel);
                    }
                })
                .subscribe();
        addDisposable(loadMovieDisposable);
    }

    void retryLoad() {
        loadMovies();
    }
}
