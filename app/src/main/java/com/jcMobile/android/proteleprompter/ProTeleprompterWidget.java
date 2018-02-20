package com.jcMobile.android.proteleprompter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.RemoteViews;

import com.jcMobile.android.proteleprompter.contentprovider.DocumentContract;
import com.jcMobile.android.proteleprompter.utilities.TeleprompterPreference;

/**
 * Implementation of App Widget functionality.
 */
public class ProTeleprompterWidget extends AppWidgetProvider {

    private static Cursor mCursor;

    private static int mFileId;

    private static String mRecentFileName;

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {
        Intent openActivityIntent, importFilesIntent;
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

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.proteleprompter_widget);

        views.setTextViewText(R.id.appwidget_text_file_name, widgetText);

        importFilesIntent = new Intent(context, MainActivity.class);
        importFilesIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        importFilesIntent.putExtra(MainActivity.IMPORT_FILE_INTENT_NAME, MainActivity.IMPORT_FILE_REQUEST_CODE);
        importFilesPendingIntent = PendingIntent.getActivity(context, 0, importFilesIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_add_ib, importFilesPendingIntent);

        if (widgetText.equals("No recent document")) {

            openActivityIntent = new Intent(context, MainActivity.class);

            views.setTextViewText(R.id.appwidget_text_recent_title, context.getString(R.string.widget_recent_no_recent_title));

            views.setViewVisibility(R.id.widget_play_ib, View.GONE);

            openActivityPendingIntent = PendingIntent.getActivity(context, 0, openActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.widget_layout, openActivityPendingIntent);

        } else {

            openActivityIntent = new Intent(context, ScrollActivity.class);

            openActivityIntent.putExtra(ScrollActivity.EXTRA_DOCUMENT_ID, mFileId);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            stackBuilder.addParentStack(ScrollActivity.class);

            stackBuilder.addNextIntent(openActivityIntent);

            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setTextViewText(R.id.appwidget_text_recent_title, context.getString(R.string.widget_recent_title));

            views.setOnClickPendingIntent(R.id.widget_layout, resultPendingIntent);

            views.setViewVisibility(R.id.widget_play_ib, View.VISIBLE);

            views.setOnClickPendingIntent(R.id.widget_play_ib, resultPendingIntent);
        }

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
            updateAppWidget(context, appWidgetManager, appWidgetId);
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
        if (id == -1 || cursor.getCount()==0) {
            fileName = context.getString(R.string.widget_no_recent_file);
        } else {
            fileName = cursor.getString(cursor.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME));
        }

        return fileName;
    }

}

