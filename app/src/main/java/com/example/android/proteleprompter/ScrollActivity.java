package com.example.android.proteleprompter;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.proteleprompter.ContentProvider.DocumentContract;


public class ScrollActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_DOCUMENT_ID = "com.android.Proteleprompter.DOCUMENT.ID";
    private static final int SINGLE_LOADER_ID = 200;
    private int mFileId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);

        mFileId = getIntent().getIntExtra(EXTRA_DOCUMENT_ID, -1);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor prefEditor = sharedPref.edit();

        prefEditor.putInt("recentFilesId", mFileId);

        prefEditor.apply();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ProTeleprompterWidget.class));
        //Trigger data update to handle widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.layout.proteleprompter_widget);
        //Now update all widgets
        ProTeleprompterWidget.updatePlantWidgets(this, appWidgetManager, appWidgetIds);

        getSupportLoaderManager().initLoader(SINGLE_LOADER_ID, null, this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri SINGLE_DOCUMENT_URI = DocumentContract.DocumentEntry.buildDocumentUri(mFileId);
        return new CursorLoader(this, SINGLE_DOCUMENT_URI, null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) return;
        data.moveToFirst();

        ActionBar actionBar = getSupportActionBar();

        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);

        actionBar.setCustomView(viewActionBar, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        String documentTitle = data.getString(data.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME));
        TextView textViewTitle = viewActionBar.findViewById(R.id.toolBar_title);
        textViewTitle.setText(documentTitle);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
