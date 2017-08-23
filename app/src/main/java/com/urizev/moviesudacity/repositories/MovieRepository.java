package com.urizev.moviesudacity.repositories;

import com.urizev.moviesudacity.repositories.cache.MovieCache;
import com.urizev.moviesudacity.repositories.services.MovieService;
import com.uwetrottmann.tmdb2.entities.BaseMovie;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class MovieRepository {

    public static final int POPULAR = 0;
    public static final int TOP_RATED = 1;
    private final MovieCache movieCache;
    private final MovieService movieService;

    public MovieRepository(MovieCache movieCache, MovieService movieService) {
        this.movieCache = movieCache;
        this.movieService = movieService;
    }

    public Observable<List<BaseMovie>> list(final int listType, final int listPage) {
        Observable<MovieResultsPage> serviceObservable = movieService.list(listType, listPage);

        return movieCache.getList(listType, listPage)
                .toObservable()
                .concatWith(serviceObservable)
                .doOnNext(new Consumer<MovieResultsPage>() {
                    @Override
                    public void accept(MovieResultsPage results) throws Exception {
                        movieCache.putList(listType, listPage, results);
                    }
                })
                .map(new Function<MovieResultsPage, List<BaseMovie>>() {
                    @Override
                    public List<BaseMovie> apply(@NonNull MovieResultsPage movieResultsPage) throws Exception {
                        return Observable.fromIterable(movieResultsPage.results)
                                .toList()
                                .blockingGet();
                    }
                });
    }

    public Observable<Movie> detail(int movieId) {
        Observable<Movie> serviceObservable = movieService.detail(movieId)
                .doOnNext(new Consumer<Movie>() {
                    @Override
                    public void accept(Movie movie) throws Exception {
                        movieCache.putDetail(movie);
                    }
                });

        return movieCache.getDetail(movieId)
                .toObservable()
                .concatWith(serviceObservable);
    }
}
