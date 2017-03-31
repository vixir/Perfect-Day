package com.vixir.finalproject.perfectday;

import android.net.Uri;
import android.provider.BaseColumns;

public class TaskItemsContract {


    public static final String AUTHORITY = "com.vixir.finalproject.perfectday";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TASK_ITEMS = "taskitems";


    private TaskItemsContract() {

    }

    public static class TaskItemsColumns implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASK_ITEMS).build();

        public static final String TABLE_NAME = "taskitems";
        public static final String COLUMN_NAME_DESCRIPTION = "task_description";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_IS_FINISHED = "is_finished";
        public static final String COLUMN_NAME_IS_TODAY = "is_today";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }

}
