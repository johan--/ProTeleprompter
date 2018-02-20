package com.jcMobile.android.proteleprompter;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class ProTeleprompterService extends IntentService {

    private static final String ACTION_UPDATE_WIDGET = "com.example.android.proteleprompter.action.updateWidget";


    public ProTeleprompterService() {
        super("ProTeleprompterService");
    }

    /**
     * Starts this service to perform update widget. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startUpdateWidget(Context context) {
        Intent intent = new Intent(context, ProTeleprompterService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WIDGET.equals(action)) {
                handleUpdateWidget();
            }
        }
    }

    /**
     * Handle update widget in the provided background thread with the provided
     * parameters.
     */
    private void handleUpdateWidget() {
        // TODO: Handle update widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ProTeleprompterWidget.class));
        //Trigger data update to handle widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.layout.proteleprompter_widget);
        //Now update all widgets
        ProTeleprompterWidget.updatePlantWidgets(this, appWidgetManager, appWidgetIds);
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
