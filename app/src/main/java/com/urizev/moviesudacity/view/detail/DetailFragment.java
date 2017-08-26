package com.urizev.moviesudacity.view.detail;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.urizev.moviesudacity.DepProvider;
import com.urizev.moviesudacity.R;
import com.urizev.moviesudacity.view.common.MVPFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends MVPFragment<DetailPresenter,DetailViewState> {
    public static final String EXTRA_MOVIE_ID = "movieId";

    @BindView(R.id.recycler) protected RecyclerView recycler;

    private int movieId;
    private DetailAdapterView adapter;

    public DetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.movieId = getArguments().getInt(EXTRA_MOVIE_ID);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
        DepProvider provider = DepProvider.getInstance();
        this.adapter = new DetailAdapterView(presenter, provider.provideImageUrlBuilder(), provider.provideImageLoader());
        this.recycler.setAdapter(adapter);
    }

    @Override
    protected void renderViewState(DetailViewState viewState) {
        adapter.setViewStates(viewState.viewStates);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_detail;
    }

    @Override
    public DetailPresenter createPresenter(Bundle savedInstanceState) {
        DepProvider provider = DepProvider.getInstance();
        return new DetailPresenter(movieId, provider.provideMovieRepository());
    }

    public static Fragment newInstance(int movieId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }
}
