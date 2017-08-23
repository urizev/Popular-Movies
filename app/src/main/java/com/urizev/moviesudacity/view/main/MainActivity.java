package com.urizev.moviesudacity.view.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.urizev.moviesudacity.DepProvider;
import com.urizev.moviesudacity.R;
import com.urizev.moviesudacity.providers.SettingsProvider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                SettingsProvider settingsProvider;
                settingsProvider = DepProvider.getInstance().provideSettingsProvider();
                new MaterialDialog.Builder(this)
                        .title(R.string.action_sort)
                        .items(R.array.sort_by_options)
                        .itemsCallbackSingleChoice(settingsProvider.getListType(), new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                DepProvider.getInstance().provideSettingsProvider().setListType(which);
                                return true;
                            }
                        })
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
