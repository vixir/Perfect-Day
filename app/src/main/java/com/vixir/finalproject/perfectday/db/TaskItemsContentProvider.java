package com.vixir.finalproject.perfectday.db;

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

import static com.vixir.finalproject.perfectday.db.TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY;
import static com.vixir.finalproject.perfectday.db.TaskItemsContract.TaskItemsColumns.TABLE_NAME;


public class TaskItemsContentProvider extends ContentProvider {

    private TaskItemsDbHelper mTaskItemsDbHelper;

    public static final int ITEM_TASKS = 100;
    public static final int ITEM_TASKS_WITH_ID = 101;
    private static final int ITEM_TASKS_TODAY = 102;
    public static final UriMatcher sUriMATCHER = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TaskItemsContract.AUTHORITY, TaskItemsContract.PATH_TASK_ITEMS, ITEM_TASKS);
        uriMatcher.addURI(TaskItemsContract.AUTHORITY, TaskItemsContract.PATH_TASK_ITEMS + "/#", ITEM_TASKS_WITH_ID);
        uriMatcher.addURI(TaskItemsContract.AUTHORITY, TaskItemsContract.PATH_TASK_ITEMS + "/" + COLUMN_NAME_IS_TODAY + "/#", ITEM_TASKS_TODAY);
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
                returnCursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case ITEM_TASKS_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                String mSelection = TaskItemsContract.TaskItemsColumns._ID + " = ?";
                String[] mSelectionArgs = new String[]{id};
                returnCursor = db.query(TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, null);
                break;
            }
            case ITEM_TASKS_TODAY: {
                String isToday = uri.getPathSegments().get(2);
                String mSelection = COLUMN_NAME_IS_TODAY + " = ?";
                String[] mSelectionArgs = new String[]{isToday};
                returnCursor = db.query(TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, null);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMATCHER.match(uri);

        switch (match) {
            case ITEM_TASKS:
                return "vnd.android.cursor.dir" + "/" + TaskItemsContract.AUTHORITY + "/" + TaskItemsContract.PATH_TASK_ITEMS;
            case ITEM_TASKS_WITH_ID:
                return "vnd.android.cursor.item" + "/" + TaskItemsContract.AUTHORITY + "/" + TaskItemsContract.PATH_TASK_ITEMS;
            case ITEM_TASKS_TODAY:
                return "vnd.android.cursor.dir" + "/" + TaskItemsContract.AUTHORITY + "/" + TaskItemsContract.PATH_TASK_ITEMS + "/" + COLUMN_NAME_IS_TODAY;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mTaskItemsDbHelper.getWritableDatabase();
        int match = sUriMATCHER.match(uri);
        Uri returnUri = null;
        switch (match) {
            case ITEM_TASKS:
                long id = db.insert(TABLE_NAME, null, values);
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
        final SQLiteDatabase db = mTaskItemsDbHelper.getWritableDatabase();
        int match = sUriMATCHER.match(uri);
        int returnIdx;
        switch (match) {
            case ITEM_TASKS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                returnIdx = db.delete(TABLE_NAME, mSelection, mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        if (returnIdx != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnIdx;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int tasksUpdated;
        final SQLiteDatabase db = mTaskItemsDbHelper.getWritableDatabase();
        int match = sUriMATCHER.match(uri);

        switch (match) {
            case ITEM_TASKS_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                tasksUpdated = db.update(TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return tasksUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mTaskItemsDbHelper.getWritableDatabase();
        switch (sUriMATCHER.match(uri)) {
            case ITEM_TASKS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
