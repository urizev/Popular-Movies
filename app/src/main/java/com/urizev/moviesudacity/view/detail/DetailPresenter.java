package com.urizev.moviesudacity.view.detail;

import com.urizev.moviesudacity.repositories.MovieRepository;
import com.urizev.moviesudacity.view.common.Presenter;
import com.uwetrottmann.tmdb2.entities.Movie;

import java.util.Calendar;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;

class DetailPresenter extends Presenter<DetailViewState> {
    private final BehaviorSubject<DetailModel> model = BehaviorSubject.create();
    private final int movieId;
    private final MovieRepository movieRepository;

    DetailPresenter(int movieId, MovieRepository movieRepository) {
        this.movieId = movieId;
        this.movieRepository = movieRepository;

        loadDetails();
        addDisposable(model
                .map(new Function<DetailModel, DetailViewState>() {
                    @Override
                    public DetailViewState apply(@NonNull DetailModel model) throws Exception {
                        if (model.error == null) {
                            Movie movie = model.movie;
                            Calendar releaseDate = Calendar.getInstance();
                            releaseDate.setTime(movie.release_date);
                            return new DetailViewState(movie.original_title, movie.poster_path, releaseDate.get(Calendar.YEAR), movie.runtime, movie.vote_average, movie.tagline, movie.overview);
                        }
                        else {
                            return new DetailViewState(model.error);
                        }
                    }
                }).doOnNext(new Consumer<DetailViewState>() {
                    @Override
                    public void accept(DetailViewState viewState) throws Exception {
                        publishViewState(viewState);
                    }
                }).subscribe());
    }

    void retryLoad() {
        loadDetails();
    }

    private void loadDetails() {
        addDisposable(movieRepository.detail(movieId)
                .map(new Function<Movie, DetailModel>() {
                    @Override
                    public DetailModel apply(Movie movie) throws Exception {
                        return DetailModel.withMovie(movie);
                    }
                })
                .onErrorReturn(new Function<Throwable,DetailModel>() {
                    @Override
                    public DetailModel apply(Throwable throwable) throws Exception {
                        return model.getValue().withError(throwable);
                    }
                })
                .doOnNext(new Consumer<DetailModel>() {
                    @Override
                    public void accept(DetailModel newModel) throws Exception {
                        model.onNext(newModel);
                    }
                })
                .subscribe());
    }
}
