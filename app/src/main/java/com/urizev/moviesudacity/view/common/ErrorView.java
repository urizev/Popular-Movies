package com.urizev.moviesudacity.view.common;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.urizev.moviesudacity.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ErrorView extends LinearLayout {
    @BindView(R.id.message)
    protected TextView message;

    public ErrorView(Context context) {
        this(context, null, 0);
    }

    public ErrorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        View.inflate(context, R.layout.view_error, this);
        ButterKnife.bind(this, this);
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }
}
