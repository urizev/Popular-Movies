package com.urizev.moviesudacity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {
    private static final NumberFormat RATING_FORMAT = DecimalFormat.getNumberInstance(Locale.getDefault());

    public static String formatDuration(int runtime) {
        int hours = runtime / 60;
        int minutes = runtime % 60;
        if (hours > 0) {
            return String.format(Locale.US, "%dh %dm", hours, minutes);
        }
        else {
            return String.format(Locale.US, "%dm", minutes);
        }
    }

    public static String formatRating(double rating) {
        return RATING_FORMAT.format(rating);
    }
}
