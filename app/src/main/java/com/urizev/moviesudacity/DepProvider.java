package com.urizev.moviesudacity;


import android.annotation.SuppressLint;
import android.content.Context;

import com.squareup.picasso.Picasso;
import com.urizev.moviesudacity.providers.ExtendedTmdb;
import com.urizev.moviesudacity.providers.ImageUrlBuilder;
import com.urizev.moviesudacity.providers.SettingsProvider;
import com.urizev.moviesudacity.repositories.MovieRepository;
import com.urizev.moviesudacity.repositories.cache.MovieCache;
import com.urizev.moviesudacity.repositories.services.MovieService;
import com.uwetrottmann.tmdb2.Tmdb;

public class DepProvider {
    @SuppressLint("StaticFieldLeak")
    private static DepProvider instance;
    private Context context;
    private MovieRepository movieRepository;
    private MovieService movieService;
    private MovieCache movieCache;
    private Tmdb tmdb;
    private Picasso picasso;
    private ImageUrlBuilder imageUrlBuilder;
    private SettingsProvider settingsProvider;

    public static DepProvider getInstance() {
        if (instance == null) {
            instance = new DepProvider();
        }
        return instance;
    }


    void setApplicationContext(Context context) {
        this.context = context;
    }

    public MovieRepository provideMovieRepository() {
        if (movieRepository == null) {
            movieRepository = new MovieRepository(context, provideMovieCache(), provideMovieService());
        }
        return movieRepository;
    }

    private MovieCache provideMovieCache() {
        if (movieCache == null) {
            movieCache = new MovieCache();
        }
        return movieCache;
    }

    private MovieService provideMovieService() {
        if (movieService == null) {
            movieService = new MovieService(provideTmdb());
        }
        return movieService;
    }

    private Tmdb provideTmdb() {
        if (tmdb == null) {
            tmdb = new ExtendedTmdb();
        }
        return tmdb;
    }

    public Picasso provideImageLoader() {
        if (picasso == null) {
            picasso = new Picasso.Builder(context).build();
        }
        return picasso;
    }

    public ImageUrlBuilder provideImageUrlBuilder() {
        if (imageUrlBuilder == null) {
            imageUrlBuilder = new ImageUrlBuilder();
        }
        return imageUrlBuilder;
    }

    public SettingsProvider provideSettingsProvider() {
        if (settingsProvider == null) {
            settingsProvider = new SettingsProvider(context);
        }
        return settingsProvider;
    }

    public Context getApplicationContext() {
        return context;
    }
}
