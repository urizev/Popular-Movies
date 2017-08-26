package com.urizev.moviesudacity.view.detail;

import com.google.common.collect.ImmutableList;
import com.urizev.moviesudacity.view.common.ViewState;

import java.util.Locale;

class DetailViewState implements ViewState {
    final ImmutableList<ViewState> viewStates;

    DetailViewState(ImmutableList<ViewState> viewStates) {
        this.viewStates = viewStates;
    }


    static class DetailInfoViewState implements ViewState {
        final String title;
        final String posterPath;
        final int year;
        final int runtime;
        final double voteAverage;
        final boolean favorite;

        DetailInfoViewState(String title, String posterPath, int year, Integer runtime, Double voteAverage, boolean favorite) {
            this.title = title;
            this.posterPath = posterPath;
            this.year = year;
            this.runtime = runtime != null ? runtime : 0;
            this.voteAverage = voteAverage != null ? voteAverage : 0;
            this.favorite = favorite;
        }
    }

    static class DetailTaglineViewState implements ViewState {
        final String outline;

        DetailTaglineViewState(String outline) {
            this.outline = outline;
        }
    }

    static class DetailOverviewViewState implements ViewState {
        final String plot;

        DetailOverviewViewState(String plot) {
            this.plot = plot;
        }
    }

    static class DetailHeaderViewState implements ViewState {
        final String title;

        DetailHeaderViewState(String title) {
            this.title = title;
        }
    }

    static class DetailTrailerViewState implements ViewState {
        private static final String BASE_URL_YOUTUBE_THUMB = "https://img.youtube.com/vi/%s/1.jpg";
        private static final String BASE_URL_YOUTUBE_WATCH = "https://www.youtube.com/watch?v=%s";
        private static final String SITE_YOUTUBE = "YouTube";

        final String title;
        final String type;
        final String key;
        final String site;

        DetailTrailerViewState(String title, String type, String site, String key) {
            this.title = title;
            this.type = type;
            this.site = site;
            this.key = key;
        }

        String getVideoScreenshotUrl() {
            switch (site) {
                case SITE_YOUTUBE:
                    return String.format(Locale.US, BASE_URL_YOUTUBE_THUMB, key);
                default:
                    return null;
            }
        }

        String getVideoWatchUrl() {
            switch (site) {
                case SITE_YOUTUBE:
                    return String.format(Locale.US, BASE_URL_YOUTUBE_WATCH, key);
                default:
                    return null;
            }
        }
    }

    static class DetailReviewViewState implements ViewState {
        final String author;
        final String content;

        DetailReviewViewState(String author, String content) {
            this.author = author;
            this.content = content;
        }
    }
}
