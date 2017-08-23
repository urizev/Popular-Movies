package com.urizev.moviesudacity.providers;

import com.urizev.moviesudacity.BuildConfig;
import com.uwetrottmann.tmdb2.Tmdb;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


public class ExtendedTmdb extends Tmdb {

    public ExtendedTmdb() {
        super(BuildConfig.TMDB_API_KEY);
    }

    @Override
    protected void setOkHttpClientDefaults(OkHttpClient.Builder builder) {
        super.setOkHttpClientDefaults(builder);
        builder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
    }
}
