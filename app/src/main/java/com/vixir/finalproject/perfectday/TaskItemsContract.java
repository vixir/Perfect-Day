package com.vixir.finalproject.perfectday;

import android.provider.BaseColumns;

public class TaskItemsContract {

    private TaskItemsContract() {

    }

    public static class TaskItemsColumns implements BaseColumns {
        public static final String TABLE_NAME = "taskitems";
        public static final String COLUMN_NAME_DESCRIPTION = "task_description";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_IS_FINISHED = "is_finished";
        public static final String COLUMN_NAME_IS_TODAY = "is_today";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }

}
