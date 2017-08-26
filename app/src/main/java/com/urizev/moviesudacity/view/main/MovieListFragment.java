package com.urizev.moviesudacity.view.main;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.urizev.moviesudacity.EndlessRecyclerViewScrollListener;
import com.urizev.moviesudacity.DepProvider;
import com.urizev.moviesudacity.R;
import com.urizev.moviesudacity.view.common.ErrorView;
import com.urizev.moviesudacity.view.common.LoadingView;
import com.urizev.moviesudacity.view.common.MVPFragment;
import com.urizev.moviesudacity.view.detail.DetailActivity;
import com.urizev.moviesudacity.view.detail.DetailFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends MVPFragment<MovieListPresenter,MovieListViewState> implements MovieListAdapter.MovieListAdapterListener {
    private static final String KEY_LIST_STATE = "listState";
    @BindView(R.id.content) protected RecyclerView contentView;
    @BindView(R.id.loading) protected LoadingView loadingView;
    @BindView(R.id.error) protected ErrorView errorView;
    private final MovieListAdapter adapter;

    public MovieListFragment() {
        this.adapter = new MovieListAdapter(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        renderLoading();
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_LIST_STATE)) {
            Parcelable listState = savedInstanceState.getParcelable(KEY_LIST_STATE);
            contentView.getLayoutManager().onRestoreInstanceState(listState);
        }
        contentView.setAdapter(adapter);
        PagerScrollListener pageListener = new PagerScrollListener((GridLayoutManager) contentView.getLayoutManager());
        contentView.addOnScrollListener(pageListener);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void renderViewState(MovieListViewState viewState) {
        Timber.d("Render viewState: " + viewState);
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(viewState.title);
        }
        if (viewState.loading) {
            this.renderLoading ();
        }
        else if (viewState.throwable != null) {
            this.renderError(viewState.throwable);
        }
        else {
            this.renderContent(viewState);
        }
    }

    private void renderContent(MovieListViewState viewState) {
        this.loadingView.setVisibility(View.INVISIBLE);
        this.errorView.setVisibility(View.INVISIBLE);
        this.contentView.setVisibility(View.VISIBLE);
        adapter.setViewStates(viewState.listViewState);
    }

    private void renderError(Throwable throwable) {
        this.loadingView.setVisibility(View.INVISIBLE);
        this.contentView.setVisibility(View.INVISIBLE);
        this.errorView.setVisibility(View.VISIBLE);
        this.errorView.setMessage(throwable.getMessage());
    }

    private void renderLoading() {
        this.loadingView.setVisibility(View.VISIBLE);
        this.contentView.setVisibility(View.INVISIBLE);
        this.errorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_movie_list;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Parcelable listState = contentView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(KEY_LIST_STATE, listState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public MovieListPresenter createPresenter(Bundle savedInstanceState) {
        DepProvider provider = DepProvider.getInstance();
        return new MovieListPresenter(provider.getApplicationContext(), provider.provideMovieRepository(), provider.provideSettingsProvider());
    }

    @OnClick(R.id.error)
    public void onErrorClicked() {
        presenter.retryLoad();
    }

    @Override
    public void onMovieClicked(int movieId) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailFragment.EXTRA_MOVIE_ID, movieId);
        this.startActivity(intent);
    }

    private class PagerScrollListener extends EndlessRecyclerViewScrollListener {
        PagerScrollListener(GridLayoutManager layoutManager) {
            super(layoutManager);
        }

        @Override
        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
            presenter.loadMoreMovies();
        }
    }
}
