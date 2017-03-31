package com.vixir.finalproject.perfectday;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class TaskItemsContentProvider extends ContentProvider {

    private TaskItemsDbHelper mTaskItemsDbHelper;

    public static final int ITEM_TASKS = 100;
    public static final int ITEM_TASKS_WITH_ID = 101;
    public static final UriMatcher sUriMATCHER = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TaskItemsContract.AUTHORITY, TaskItemsContract.PATH_TASK_ITEMS, ITEM_TASKS);
        uriMatcher.addURI(TaskItemsContract.AUTHORITY, TaskItemsContract.PATH_TASK_ITEMS + "/#", ITEM_TASKS);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mTaskItemsDbHelper = new TaskItemsDbHelper(context);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mTaskItemsDbHelper.getReadableDatabase();
        int match = sUriMATCHER.match(uri);
        Cursor returnCursor;
        switch (match) {
            case ITEM_TASKS:
                returnCursor = db.query(TaskItemsContract.PATH_TASK_ITEMS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mTaskItemsDbHelper.getWritableDatabase();
        int match = sUriMATCHER.match(uri);
        Uri returnUri = null;
        switch (match) {
            case ITEM_TASKS:
                long id = db.insert(TaskItemsContract.PATH_TASK_ITEMS, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TaskItemsContract.TaskItemsColumns.CONTENT_URI, id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
