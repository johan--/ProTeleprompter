package com.example.android.proteleprompter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.proteleprompter.ContentProvider.DocumentContract;
import com.example.android.proteleprompter.Utilities.TeleprompterPreference;

/**
 * Implementation of App Widget functionality.
 */
public class ProTeleprompterWidget extends AppWidgetProvider {

    private static Cursor mCursor;

    private static int mFileId;

    private static String mRecentFileName;

    private static final String ACTION_PROTELEPROMPTER = "PROTELEPROMPTER_WIDGET";

    private static final int READ_REQUEST_CODE = 39;

    private static Intent importFilesIntent;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Intent openActivityIntent;
        PendingIntent openActivityPendingIntent, importFilesPendingIntent;

        mFileId = TeleprompterPreference.getRecentFileId(context);

        String[] projection = {DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME};
        String selection = DocumentContract.DocumentEntry._ID + " =?";
        String[] selectionArgs = {String.valueOf(mFileId)};
        mCursor = context.getContentResolver().query(
                DocumentContract.DocumentEntry.CONTENT_URI,
                projection, selection, selectionArgs, null);

        mRecentFileName = retrieveRecentFileName(context, mFileId, mCursor);

        CharSequence widgetText = mRecentFileName;

        performFileSearch();
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.proteleprompter_widget);

        views.setTextViewText(R.id.appwidget_text_file_name, widgetText);

        if (widgetText.equals("No recent document")) {

            openActivityIntent = new Intent(context, MainActivity.class);

            views.setTextViewText(R.id.appwidget_text_recent, context.getString(R.string.widget_recent_no_recent_title));

            views.setViewVisibility(R.id.widget_play_ib, View.GONE);

        } else {

            views.setViewVisibility(R.id.widget_play_ib, View.VISIBLE);

            openActivityIntent = new Intent(context, ScrollActivity.class);

            openActivityIntent.putExtra(ScrollActivity.EXTRA_DOCUMENT_ID, mFileId);
        }

        openActivityPendingIntent = PendingIntent.getActivity(context, 0, openActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        importFilesPendingIntent = PendingIntent.getActivity(context, 0, importFilesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.widget_layout, openActivityPendingIntent);

        views.setOnClickPendingIntent(R.id.widget_add_ib, importFilesPendingIntent);

        mCursor.close();
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager,
                                          int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager,appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        mFileId = TeleprompterPreference.getRecentFileId(context);

        String[] projection = {DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME};
        String selection = DocumentContract.DocumentEntry._ID + " =?";
        String[] selectionArgs = {String.valueOf(mFileId)};
        mCursor = context.getContentResolver().query(
                DocumentContract.DocumentEntry.CONTENT_URI,
                projection, selection, selectionArgs, null);

        mRecentFileName = retrieveRecentFileName(context, mFileId, mCursor);

        mCursor.close();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static String retrieveRecentFileName(Context context, int id, Cursor cursor) {
        String fileName;
        cursor.moveToFirst();

        if (id == -1) {
            fileName = context.getString(R.string.widget_no_recent_file);
        } else {

            fileName = cursor.getString(cursor.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME));
        }

        return fileName;
    }

    private static void performFileSearch() {

        importFilesIntent = new Intent(Intent.ACTION_GET_CONTENT);

        importFilesIntent.addCategory(Intent.CATEGORY_OPENABLE);

//        String[] mimeTypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
//                "text/plain",
//                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
//                "application/msword",
//                "application/vnd.ms-powerpoint",
//                "application/vnd.ms-powerpoint",
//                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
//                "application/rtf",
//                "application/x-rtf",
//                "text/richtext",
//                "application/vnd.google-apps.document",
//                "application/pdf",
//                "text/rtf"};

        importFilesIntent.setType("text/plain");

        //startActivity(intent, READ_REQUEST_CODE);

    }
}

