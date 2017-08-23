package com.urizev.moviesudacity.repositories.services;

import com.urizev.moviesudacity.repositories.MovieRepository;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.services.MoviesService;

import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;

public class MovieService {
    private final MoviesService movieService;

    public MovieService(Tmdb tmdb) {
        this.movieService = tmdb.moviesService();
    }
    public Observable<MovieResultsPage> list(final int list, final int page) {
        return Observable.fromCallable(new Callable<MovieResultsPage>() {
                    @Override
                    public MovieResultsPage call() throws Exception {
                        Call<MovieResultsPage> call;
                        switch (list) {
                            case MovieRepository.TOP_RATED:
                                call = movieService.topRated(page, Locale.getDefault().getLanguage());
                                break;
                            case MovieRepository.POPULAR:
                            default:
                                call = movieService.popular(page, Locale.getDefault().getLanguage());
                        }

                        return call.execute().body();
                    }
                })
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Movie> detail(final int movieId) {
        return Observable.fromCallable(new Callable<Movie>() {
                    @Override
                    public Movie call() throws Exception {
                        return movieService
                                .summary(movieId, Locale.getDefault().getLanguage())
                                .execute()
                                .body();
                    }
                })
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io());
    }
}
