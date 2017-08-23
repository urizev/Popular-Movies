package com.urizev.moviesudacity.providers;

public class ImageUrlBuilder {
    private static final String TMDB_IMAGE_URL_BASE = "http://image.tmdb.org/t/p/";
    private static final int [] IMAGE_STEPS = { 92, 154, 185, 342, 500, 780, Integer.MAX_VALUE};

    public String build(String path, int width) {
        int size = IMAGE_STEPS[0];
        boolean original = true;
        for (int step : IMAGE_STEPS) {
            if (step > width) {
                original = false;
                break;
            }
            size = step;
        }

        String sizePath = original ? "original" : "w" + size;
        String url = TMDB_IMAGE_URL_BASE + sizePath + path;

        return url;
    }
}
