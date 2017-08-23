package com.urizev.moviesudacity.view.main;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.urizev.moviesudacity.providers.ImageUrlBuilder;
import com.urizev.moviesudacity.DepProvider;
import com.urizev.moviesudacity.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {
    private final Picasso imageLoader;
    private final ImageUrlBuilder imageUrlBuilder;
    private final MovieListAdapterListener listener;
    private List<MovieViewState> viewStates;


    MovieListAdapter(MovieListAdapterListener listener) {
        imageLoader = DepProvider.getInstance().provideImageLoader();
        imageUrlBuilder = DepProvider.getInstance().provideImageUrlBuilder();
        this.listener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_movie_list, parent, false));
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(viewStates.get(position));
    }

    @Override
    public int getItemCount() {
        return viewStates == null ? 0 : viewStates.size();
    }

    void setViewStates(List<MovieViewState> viewStates) {
        this.viewStates = viewStates;
        this.notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final int itemWidth;
        @BindView(R.id.poster) ImageView poster;
        MovieViewHolder(View itemView) {
            super(itemView);
            Resources res = itemView.getContext().getResources();
            itemWidth = res.getDisplayMetrics().widthPixels / res.getInteger(R.integer.movie_list_columns);
            ButterKnife.bind(this, itemView);
            poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMovieClicked(viewStates.get(getAdapterPosition()).id);
                }
            });
        }

        void bind(MovieViewState movieViewState) {
            String url = imageUrlBuilder.build(movieViewState.poster, itemWidth);
            imageLoader.load(url).into(poster);
        }
    }

    interface MovieListAdapterListener {
        void onMovieClicked(int movieId);
    }
}
