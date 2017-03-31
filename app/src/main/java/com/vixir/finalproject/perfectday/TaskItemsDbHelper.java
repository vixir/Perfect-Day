package com.vixir.finalproject.perfectday;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vixir.finalproject.perfectday.TaskItemsContract.TaskItemsColumns;

public class TaskItemsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "taskitems.db";

    private static final int DATABASE_VERSION = 1;

    public TaskItemsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    TaskItemsContract taskItemsContract = null;

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TASKITEMS_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TaskItemsColumns.TABLE_NAME + " ( "
                + TaskItemsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TaskItemsColumns.COLUMN_NAME_DESCRIPTION + " TEXT, "
                + TaskItemsColumns.COLUMN_NAME_COLOR + " TEXT NOT NULL DEFAULT '#ffffff', "
                + TaskItemsColumns.COLUMN_NAME_IS_FINISHED + " INTEGER NOT NULL DEFAULT 0, "
                + TaskItemsColumns.COLUMN_NAME_IS_TODAY + " INTEGER NOT NULL DEFAULT 1, "
                + TaskItemsColumns.COLUMN_NAME_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                + ", CONSTRAINT unique_task_description UNIQUE (task_description) ON CONFLICT REPLACE"
                + " );";

        db.execSQL(SQL_CREATE_TASKITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskItemsColumns.TABLE_NAME);
        onCreate(db);
    }
}
