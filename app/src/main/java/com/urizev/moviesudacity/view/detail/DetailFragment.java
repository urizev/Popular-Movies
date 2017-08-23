package com.urizev.moviesudacity.view.detail;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.urizev.moviesudacity.DepProvider;
import com.urizev.moviesudacity.FormatUtils;
import com.urizev.moviesudacity.R;
import com.urizev.moviesudacity.providers.ImageUrlBuilder;
import com.urizev.moviesudacity.view.common.MVPFragment;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends MVPFragment<DetailPresenter,DetailViewState> {
    public static final String EXTRA_MOVIE_ID = "movieId";

    @BindView(R.id.content) protected View content;
    @BindView(R.id.error) protected View error;
    @BindView(R.id.poster) protected ImageView poster;
    @BindView(R.id.title) protected TextView title;
    @BindView(R.id.year) protected TextView year;
    @BindView(R.id.duration) protected TextView duration;
    @BindView(R.id.rating) protected TextView rating;
    @BindView(R.id.outline) protected TextView outline;
    @BindView(R.id.plot) protected TextView plot;

    private ImageUrlBuilder imageUrlBuilder;
    private Picasso imageLoader;
    private float posterWidth;
    private int movieId;

    public DetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.movieId = getArguments().getInt(EXTRA_MOVIE_ID);
        this.imageUrlBuilder = DepProvider.getInstance().provideImageUrlBuilder();
        this.imageLoader = DepProvider.getInstance().provideImageLoader();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        this.posterWidth = this.getResources().getDimension(R.dimen.detail_poster_width);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void renderViewState(DetailViewState viewState) {
        if (viewState.error == null) {
            String url = imageUrlBuilder.build(viewState.posterPath, (int) posterWidth);
            imageLoader.load(url).into(poster);
            title.setText(viewState.title);
            year.setText(String.format(Locale.US, "%d", viewState.year));
            duration.setText(FormatUtils.formatDuration(viewState.runtime));
            rating.setText(FormatUtils.formatRating(viewState.voteAverage));
            outline.setText(viewState.tagline);
            plot.setText(viewState.overview);
            error.setVisibility(View.INVISIBLE);
            content.setVisibility(View.VISIBLE);
        }
        else {
            error.setVisibility(View.VISIBLE);
            content.setVisibility(View.INVISIBLE);
        }
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



    @OnClick(R.id.error)
    public void onErrorClicked() {
        presenter.retryLoad();
    }

    public static Fragment newInstance(int movieId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }
}
