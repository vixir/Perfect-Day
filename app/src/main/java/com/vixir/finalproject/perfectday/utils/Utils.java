package com.vixir.finalproject.perfectday.utils;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.vixir.finalproject.perfectday.db.TaskItemsContract;

public class Utils {

    public static void editItem(FragmentActivity activity, int id, String inputText, int color) {
        Uri uri = TaskItemsContract.TaskItemsColumns.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COLOR, color);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_DESCRIPTION, inputText);
        uri = uri.buildUpon().appendPath(String.valueOf(id)).build();
        activity.getContentResolver().update(uri, contentValues, null, null);
    }

    public static void addItem(FragmentActivity activity, String description, int color) {
        if (description.length() == 0) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_DESCRIPTION, description);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED, 0);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY, 1);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COLOR, color);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_CREATED_AT, System.currentTimeMillis());
        Uri uri = activity.getContentResolver().insert(TaskItemsContract.TaskItemsColumns.CONTENT_URI, contentValues);
        if (uri != null) {
            Toast.makeText(activity, uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


}
