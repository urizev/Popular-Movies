package com.urizev.moviesudacity;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;


public class PosterView extends android.support.v7.widget.AppCompatImageView {
    private static final float ASPECT_RATIO = 1.5f;

    public PosterView(Context context) {
        this(context, null, 0);
    }

    public PosterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PosterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int newWidth = getMeasuredWidth();
        int newHeight = (int) (newWidth * ASPECT_RATIO);

        setMeasuredDimension(newWidth, newHeight);
    }
}
