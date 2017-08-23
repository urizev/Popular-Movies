package com.urizev.moviesudacity.providers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.urizev.moviesudacity.repositories.MovieRepository;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class SettingsProvider {
    public static final String KEY_LIST_ORDER = "listOrder";

    private final SharedPreferences prefs;

    public SettingsProvider(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Observable<SettingsChangedEvent> observeEvents() {
        return Observable.create(new ObservableOnSubscribe<SettingsChangedEvent>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<SettingsChangedEvent> emitter) throws Exception {
                final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(new SettingsChangedEvent(sharedPreferences, key));
                        }
                    }
                };
                prefs.registerOnSharedPreferenceChangeListener(listener);
                emitter.setDisposable(new Disposable() {
                    private boolean isDisposed;

                    @Override
                    public void dispose() {
                        this.isDisposed = true;
                        prefs.unregisterOnSharedPreferenceChangeListener(listener);
                    }

                    @Override
                    public boolean isDisposed() {
                        return isDisposed;
                    }
                });
            }
        });
    }

    public void setListType(int order) {
        prefs.edit().putInt(KEY_LIST_ORDER, order).apply();
    }

    public int getListType() {
        return prefs.getInt(KEY_LIST_ORDER, MovieRepository.POPULAR);
    }

    public class SettingsChangedEvent {
        public final SharedPreferences sharedPreferences;
        public final String key;

        SettingsChangedEvent(SharedPreferences sharedPreferences, String key) {
            this.sharedPreferences = sharedPreferences;
            this.key = key;
        }
    }
}
