package com.vixir.finalproject.perfectday;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vixir.finalproject.perfectday.TaskItemsContract;
import com.vixir.finalproject.perfectday.TodayWidgetProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateProgressTasks {

    public static final String ACTION_UPDATE_FIREBASE_DB = "update-firebase-db";
    public static final String ACTION_SEND_DATA_TO_WIDGET = "send-data-widget";
    public static final String UNIQUE_DAYS_KEY = "uniqueDays";
    public static final String ACTION_FETCH_FIREBASE_DB = "get-data-firebase";
    public static final String WIDGET_DATA = "widget-data";
    private static final String TAG = UpdateProgressTasks.class.getSimpleName();
    public static final String ACTION_UPDATE_TODAY_INFORMATION = "update-today-info";

    public static void executeTask(Context context, String action) {
        if (ACTION_UPDATE_FIREBASE_DB.equals(action)) {
            updateFirebaseDatabase(context);
        } else if (ACTION_SEND_DATA_TO_WIDGET.equals(action)) {
            sendCursorDataToWidgetProvider(context);
        } else if (ACTION_FETCH_FIREBASE_DB.equals(action)) {
            fetchDataFromFirebase(context);
        } else if (ACTION_UPDATE_TODAY_INFORMATION.equals(action)) {
            updateDateInformation(context);
        }
    }

    public static void updateDateInformation(Context context) {
        Cursor cursor;
        try {
            Uri todaysUri = TaskItemsContract.TaskItemsColumns.CONTENT_URI.buildUpon().appendPath(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY).appendPath("1").build();
            cursor = context.getContentResolver().query(todaysUri,
                    null,
                    null,
                    null,
                    null);

        } catch (Exception e) {
            Log.e(TAG, "Failed to asynchronously load data.");
            e.printStackTrace();
            return;
        }
        if (null == cursor) {
            return;
        }
        while (cursor.moveToNext()) {
            int isFinishedIndex = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED);
            int isTodayIndex = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY);
            int streakIndex = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_STREAK);
            int taskCompletedDates = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COMPLETED_DATES);
            int idIndex = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns._ID);
            ContentValues contentValues = new ContentValues();
            if (cursor.getInt(isFinishedIndex) == 1) {
                String dateList = cursor.getString(taskCompletedDates);
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = null;
                try {
                    jsonObject = new JSONObject(dateList);
                    jsonArray = jsonObject.optJSONArray(UNIQUE_DAYS_KEY);
                    if (null != jsonArray) {
                        jsonArray.put(System.currentTimeMillis() - 2 * 24 * 3600 * 1000);
                    } else {
                        jsonArray = new JSONArray(UNIQUE_DAYS_KEY);
                        jsonArray.put(System.currentTimeMillis());
                    }
                    contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COMPLETED_DATES, jsonObject.toString());
                    contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_STREAK, cursor.getInt(streakIndex) + 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED, 0);
            contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY, 0);
            Uri uri = TaskItemsContract.TaskItemsColumns.CONTENT_URI;
            uri = uri.buildUpon().appendPath(cursor.getString(idIndex)).build();
            context.getContentResolver().update(uri, contentValues, null, null);
        }
        System.out.print(TAG + "Update Today");
        Log.e(TAG, "Update Today");
    }

    private static void updateFirebaseDatabase(Context context) {
        Cursor cursor;
        try {
            cursor = context.getContentResolver().query(TaskItemsContract.TaskItemsColumns.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to asynchronously load data.");
            e.printStackTrace();
            return;
        }
        if (null == cursor) {
            return;
        }
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        Map<String, TaskItem> usersTask = new HashMap<String, TaskItem>();//
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (null == user) {
            return;
        }
        DatabaseReference userId = mDatabase.child(user.getUid()).child(TaskItemsContract.PATH_TASK_ITEMS);
        while (cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns._ID);
            int itemDescriptionIndex = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_DESCRIPTION);
            int colorIndex = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COLOR);
            int isFinishedIndex = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED);
            int isTodayIndex = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY);
            int createdOn = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_CREATED_AT);
            TaskItem taskItem = new TaskItem();
            taskItem.setColor(cursor.getInt(colorIndex));
            taskItem.setDescription(cursor.getString(itemDescriptionIndex));
            taskItem.setIsFinished(cursor.getInt(isFinishedIndex));
            taskItem.setIsToday(cursor.getInt(isTodayIndex));
            taskItem.setCreatedOn(cursor.getString(createdOn));
            usersTask.put(cursor.getString(idIndex), taskItem);
        }
        cursor.close();
        userId.setValue(usersTask);


    }

    public static void fetchDataFromFirebase(final Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final DatabaseReference child = mDatabase.child(user.getUid()).child(TaskItemsContract.PATH_TASK_ITEMS);
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ContentValues[] contentValuesArray = new ContentValues[(int) dataSnapshot.getChildrenCount()]; //tears of joy when this fails
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                int i = 0;
                for (DataSnapshot child : children) {
                    TaskItem value = child.getValue(TaskItem.class);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_DESCRIPTION, value.getDescription());
                    contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED, value.getIsFinished());
                    contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY, value.getIsToday()); // no boolean in content providers
                    contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COLOR, value.getColor());
                    contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_CREATED_AT, System.currentTimeMillis());
                    contentValuesArray[i++] = contentValues;
                }
                int uri = context.getContentResolver().bulkInsert(TaskItemsContract.TaskItemsColumns.CONTENT_URI, contentValuesArray);
                if (uri != 0) {
                    // loader refresh
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void sendCursorDataToWidgetProvider(Context context) {
        Intent intent = new Intent();
        intent.setAction(TodayWidgetProvider.ACTION_DATA_UPDATE);
        context.sendBroadcast(intent);
    }
}
