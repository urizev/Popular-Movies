package com.urizev.moviesudacity.view.detail;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.squareup.picasso.Picasso;
import com.urizev.moviesudacity.DepProvider;
import com.urizev.moviesudacity.FormatUtils;
import com.urizev.moviesudacity.R;
import com.urizev.moviesudacity.providers.ImageUrlBuilder;
import com.urizev.moviesudacity.view.common.ErrorView;
import com.urizev.moviesudacity.view.common.ErrorViewState;
import com.urizev.moviesudacity.view.common.ViewState;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class DetailAdapterView extends RecyclerView.Adapter<DetailAdapterView.ViewStateViewHolder> {
    private final float posterWidth;
    private final WeakReference<DetailAdapterViewListener> listener;
    private List<ViewState> viewStates;
    private ImageUrlBuilder imageUrlBuilder;
    private Picasso imageLoader;

    DetailAdapterView(DetailAdapterViewListener listener, ImageUrlBuilder imageUrlBuilder, Picasso imageLoader) {
        this.listener = new WeakReference<>(listener);
        this.viewStates = ImmutableList.of();
        this.imageLoader = imageLoader;
        this.imageUrlBuilder = imageUrlBuilder;
        this.posterWidth = DepProvider.getInstance().getApplicationContext().getResources().getDimension(R.dimen.detail_poster_width);
    }

    @Override
    public int getItemViewType(int position) {
        ViewState vs = viewStates.get(position);
        if (vs instanceof DetailViewState.DetailInfoViewState) {
            return R.layout.cell_detail_info;
        } else if (vs instanceof DetailViewState.DetailTaglineViewState) {
            return R.layout.cell_detail_tagline;
        } else if (vs instanceof DetailViewState.DetailOverviewViewState) {
            return R.layout.cell_detail_overview;
        } else if (vs instanceof DetailViewState.DetailHeaderViewState) {
            return R.layout.cell_detail_header;
        } else if (vs instanceof DetailViewState.DetailTrailerViewState) {
            return R.layout.cell_detail_trailer;
        } else if (vs instanceof DetailViewState.DetailReviewViewState) {
            return R.layout.cell_detail_review;
        } else if (vs instanceof ErrorViewState) {
            return R.layout.view_error;
        } else {
            return -1;
        }
    }

    @Override
    public ViewStateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.layout.cell_detail_info:
                return new DetailInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
            case R.layout.cell_detail_tagline:
                return new DetailTaglineViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
            case R.layout.cell_detail_overview:
                return new DetailPlotViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
            case R.layout.cell_detail_header:
                return new DetailHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
            case R.layout.cell_detail_trailer:
                return new DetailTrailerViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
            case R.layout.cell_detail_review:
                return new DetailReviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
            case R.layout.view_error:
                return new DetailErrorViewHolder(new ErrorView(parent.getContext()));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewStateViewHolder holder, int position) {
        //noinspection unchecked
        holder.render(viewStates.get(position));
    }

    @Override
    public int getItemCount() {
        return viewStates.size();
    }

    void setViewStates(ImmutableList<ViewState> viewStates) {
        this.viewStates = viewStates;
        this.notifyDataSetChanged();
    }

    abstract class ViewStateViewHolder<VS extends ViewState> extends ViewHolder {
        ViewStateViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void render(VS vs);
    }

    class DetailInfoViewHolder extends ViewStateViewHolder<DetailViewState.DetailInfoViewState> {
        @BindView(R.id.poster) protected ImageView poster;
        @BindView(R.id.title) protected TextView title;
        @BindView(R.id.year) protected TextView year;
        @BindView(R.id.duration) protected TextView duration;
        @BindView(R.id.rating) protected TextView rating;
        @BindView(R.id.favorite) protected ImageButton favorite;

        DetailInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void render(DetailViewState.DetailInfoViewState viewState) {
            String url = imageUrlBuilder.build(viewState.posterPath, (int) posterWidth);

            imageLoader.load(url).into(poster);
            title.setText(viewState.title);
            year.setText(String.format(Locale.US, "%d", viewState.year));
            duration.setText(FormatUtils.formatDuration(viewState.runtime));
            rating.setText(FormatUtils.formatRating(viewState.voteAverage));
            favorite.setImageResource(viewState.favorite ? R.drawable.ic_favorite_fill_red_24dp : R.drawable.ic_favorite_border_black_24dp);
        }

        @OnClick(R.id.favorite)
        void onFavoriteClicked() {
            DetailAdapterViewListener l = listener.get();
            if (l != null) {
                l.onFavoriteClicked();
            }
        }
    }

    class DetailTaglineViewHolder extends ViewStateViewHolder<DetailViewState.DetailTaglineViewState> {
        @BindView(R.id.outline) TextView outline;

        DetailTaglineViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void render(DetailViewState.DetailTaglineViewState vs) {
            outline.setText(vs.outline);
        }
    }

    class DetailPlotViewHolder extends ViewStateViewHolder<DetailViewState.DetailOverviewViewState> {
        @BindView(R.id.plot) TextView plot;

        DetailPlotViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void render(DetailViewState.DetailOverviewViewState vs) {
            plot.setText(vs.plot);
        }
    }

    class DetailHeaderViewHolder extends ViewStateViewHolder<DetailViewState.DetailHeaderViewState> {
        @BindView(R.id.title) TextView title;

        DetailHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void render(DetailViewState.DetailHeaderViewState vs) {
            title.setText(vs.title);
        }
    }

    class DetailTrailerViewHolder extends ViewStateViewHolder<DetailViewState.DetailTrailerViewState> implements View.OnClickListener {
        @BindView(R.id.screenshot) ImageView screenshot;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.subtitle) TextView subtitle;

        DetailTrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void render(DetailViewState.DetailTrailerViewState vs) {
            title.setText(vs.title);
            subtitle.setText(vs.type);
            imageLoader.load(vs.getVideoScreenshotUrl())
                    .into(screenshot);
        }

        @Override
        public void onClick(View view) {
            DetailViewState.DetailTrailerViewState vs;
            vs = (DetailViewState.DetailTrailerViewState) viewStates.get(getAdapterPosition());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(vs.getVideoWatchUrl()));
            view.getContext().startActivity(intent);
        }
    }

    class DetailReviewViewHolder extends ViewStateViewHolder<DetailViewState.DetailReviewViewState> {
        @BindView(R.id.author) TextView author;
        @BindView(R.id.content) TextView content;

        DetailReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void render(DetailViewState.DetailReviewViewState vs) {
            author.setText(vs.author);
            content.setText(vs.content);
        }
    }

    class DetailErrorViewHolder extends ViewStateViewHolder<ErrorViewState> {
        @BindView(R.id.message) TextView message;

        DetailErrorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void render(ErrorViewState vs) {
            message.setText(vs.error.getLocalizedMessage());
        }
    }

    interface DetailAdapterViewListener {
        void onFavoriteClicked();
    }
}
