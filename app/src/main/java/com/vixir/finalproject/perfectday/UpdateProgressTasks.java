package com.vixir.finalproject.perfectday;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.vixir.finalproject.perfectday.TaskItemsContract;
import com.vixir.finalproject.perfectday.TodayWidgetProvider;

import java.util.ArrayList;

public class UpdateProgressTasks {

    public static final String ACTION_UPDATE_FIREBASE_DB = "update-firebase-db";
    public static final String ACTION_SEND_DATA_TO_WIDGET = "send-data-widget";
    public static final String ACTION_FETCH_FIREBASE_DB = "get-data-firebase";
    public static final String WIDGET_DATA = "widget-data";

    public static void executeTask(Context context, String action) {
        if (ACTION_UPDATE_FIREBASE_DB.equals(action)) {
            updateFirebaseDatabase(context);
        } else if (ACTION_SEND_DATA_TO_WIDGET.equals(action)) {
            sendCursorDataToWidgetProvider(context);
        } else if (ACTION_FETCH_FIREBASE_DB.equals(action)) {
            fetchDataFromFirebase(context);
        }
    }

    private static void fetchDataFromFirebase(Context context) {

    }

    private static void updateFirebaseDatabase(Context context) {

    }

    private static void sendCursorDataToWidgetProvider(Context context) {
        Intent intent = new Intent();
        intent.setAction(TodayWidgetProvider.ACTION_DATA_UPDATE);
        context.sendBroadcast(intent);
    }
}
