package com.urizev.moviesudacity.view.detail;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.collect.ImmutableList;
import com.urizev.moviesudacity.DepProvider;
import com.urizev.moviesudacity.R;
import com.urizev.moviesudacity.repositories.MovieRepository;
import com.urizev.moviesudacity.view.common.ErrorViewState;
import com.urizev.moviesudacity.view.common.Presenter;
import com.urizev.moviesudacity.view.common.ViewState;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.Review;
import com.uwetrottmann.tmdb2.entities.Videos;

import java.util.Calendar;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;

class DetailPresenter extends Presenter<DetailViewState> implements DetailAdapterView.DetailAdapterViewListener {
    private final BehaviorSubject<DetailModel> model = BehaviorSubject.create();
    private final int movieId;
    private final MovieRepository movieRepository;

    DetailPresenter(int movieId, MovieRepository movieRepository) {
        this.movieId = movieId;
        this.movieRepository = movieRepository;

        loadDetails();
        observeModel();
    }

    private void observeModel() {
        addDisposable(model
                .map(new Function<DetailModel, DetailViewState>() {
                    @Override
                    public DetailViewState apply(@NonNull DetailModel model) throws Exception {
                        return mapMovieToDetailViewState(model);
                    }
                }).doOnNext(new Consumer<DetailViewState>() {
                    @Override
                    public void accept(DetailViewState viewState) throws Exception {
                        publishViewState(viewState);
                    }
                }).subscribe());
    }

    @android.support.annotation.NonNull
    private DetailViewState mapMovieToDetailViewState(@NonNull DetailModel model) {
        Context context = DepProvider.getInstance().getApplicationContext();

        ImmutableList.Builder<ViewState> builder = new ImmutableList.Builder<>();
        if (model.movie != null) {
            Calendar releaseDate = Calendar.getInstance();
            releaseDate.setTime(model.movie.release_date);
            builder = builder.add(new DetailViewState.DetailInfoViewState(model.movie.title, model.movie.poster_path, releaseDate.get(Calendar.YEAR), model.movie.runtime, model.movie.vote_average, model.favorite));
            if (!TextUtils.isEmpty(model.movie.tagline)) {
                builder = builder.add(new DetailViewState.DetailTaglineViewState(model.movie.tagline));
            }
            if (!TextUtils.isEmpty(model.movie.overview)) {
                builder = builder.add(new DetailViewState.DetailOverviewViewState(model.movie.overview));
            }
            if (model.movie.videos != null && !model.movie.videos.results.isEmpty()) {
                builder = builder.add(new DetailViewState.DetailHeaderViewState(context.getString(R.string.trailers_header)));
                for (Videos.Video video : model.movie.videos.results) {
                    builder = builder.add(new DetailViewState.DetailTrailerViewState(video.name, video.type, video.site, video.key));
                }
            }
            if (model.movie.reviews != null && !model.movie.reviews.results.isEmpty()) {
                builder = builder.add(new DetailViewState.DetailHeaderViewState(context.getString(R.string.reviews_header)));
                for (Review review : model.movie.reviews.results) {
                    builder = builder.add(new DetailViewState.DetailReviewViewState(review.author, review.content));
                }
            }
        }

        if (model.error != null) {
            builder = builder.add(new ErrorViewState(model.error));
        }

        return new DetailViewState(builder.build());
    }

    private void loadDetails() {
        addDisposable(movieRepository
                .detail(movieId)
                .zipWith(movieRepository.isFavorite(movieId).toObservable(),
                        new BiFunction<Movie, Boolean, DetailModel>() {

                            @Override
                            public DetailModel apply(@NonNull Movie movie, @NonNull Boolean fav) throws Exception {
                                return DetailModel.withMovie(movie, fav);                            }
                        })
                .onErrorReturn(new Function<Throwable,DetailModel>() {
                    @Override
                    public DetailModel apply(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        DetailModel currentModel = model.getValue();
                        if (currentModel != null) {
                            return model.getValue().withError(throwable, currentModel.favorite);
                        }
                        else {
                            return DetailModel.withMovie(null, false).withError(throwable, false);
                        }
                    }
                })
                .doOnNext(new Consumer<DetailModel>() {
                    @Override
                    public void accept(DetailModel newModel) throws Exception {
                        model.onNext(newModel);
                    }
                })
                .subscribe());
    }

    @Override
    public void onFavoriteClicked() {
        final DetailModel currentModel = this.model.getValue();
        if (currentModel.movie != null) {
            addDisposable(movieRepository.markAsFavorite(currentModel.movie, !currentModel.favorite)
                    .doOnComplete(new Action() {
                        @Override
                        public void run() throws Exception {
                            model.onNext(currentModel.withFavorite(!currentModel.favorite));
                        }
                    })
                    .doFinally(new Action() {
                        @Override
                        public void run() throws Exception {
                            loadDetails();
                        }
                    })
                    .subscribe());
        }
    }
}
