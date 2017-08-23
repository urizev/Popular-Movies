package com.urizev.moviesudacity;

import android.app.Application;

import timber.log.Timber;


public class App extends Application {
    @Override
    public void onCreate() {
        DepProvider.getInstance().setApplicationContext(this);
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
