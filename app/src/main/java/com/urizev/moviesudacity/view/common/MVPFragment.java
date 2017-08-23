package com.urizev.moviesudacity.view.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.urizev.moviesudacity.AndroidUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public abstract class MVPFragment<P extends Presenter<VS>, VS extends ViewState> extends Fragment {
    protected P presenter;
    protected CompositeDisposable disposables;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(this.getLayoutRes(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        disposables = new CompositeDisposable();

        presenter = createPresenter(savedInstanceState);
        addDisposable(presenter.viewStateObservable()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<VS>() {
                    @Override
                    public void accept(VS viewState) throws Exception {
                        AndroidUtils.assertMainThread();
                        renderViewState(viewState);
                    }
                }).subscribe());
    }

    @Override
    public void onDestroyView() {
        disposables.dispose();
        presenter.dispose();
        super.onDestroyView();
    }

    private void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }

    protected abstract void renderViewState(VS viewState);
    protected abstract int getLayoutRes();
    protected abstract P createPresenter(Bundle savedInstanceState);
}
