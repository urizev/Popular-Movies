package com.urizev.moviesudacity.view.main;

import com.google.common.collect.ImmutableList;
import com.uwetrottmann.tmdb2.entities.BaseMovie;

import java.util.List;

class MovieListModel {
    final boolean loading;
    final Throwable error;
    final int listType;
    final int listPage;
    final ImmutableList<BaseMovie> movies;

    private MovieListModel(int listType, int listPage, ImmutableList<BaseMovie> movies, boolean loading, Throwable error) {
        this.loading = loading;
        this.listType = listType;
        this.listPage = listPage;
        this.movies = movies;
        this.error = error;
    }

    static MovieListModel newModelWidthListType(int listType) {
        return new MovieListModel(listType, 1, ImmutableList.<BaseMovie>of(), true, null);
    }

    MovieListModel nextPageModel() {
        return new MovieListModel(listType, listPage + 1, movies, false, null);
    }

    MovieListModel withAppendedMovies(List<BaseMovie> movies) {
        ImmutableList.Builder<BaseMovie> builder = new ImmutableList.Builder<>();
        builder = builder.addAll(this.movies);
        builder = builder.addAll(movies);
        return new MovieListModel(listType, listPage, builder.build(), false, null);
    }

    MovieListModel withError(Throwable error) {
        return new MovieListModel(listType, listPage, movies, false, error);
    }
}
