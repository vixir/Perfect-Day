package com.vixir.finalproject.perfectday.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vixir.finalproject.perfectday.R;
import com.vixir.finalproject.perfectday.UpdateProgressIntentService;
import com.vixir.finalproject.perfectday.db.TaskItemsContract;

import static android.content.Context.MODE_PRIVATE;
import static com.vixir.finalproject.perfectday.utils.Constants.FIRST_TIME_LOGIN;

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
            showWhiteSnackBar(R.string.item_added, (AppCompatActivity) activity);
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


    public static void showWhiteSnackBar(int signed_in_message, AppCompatActivity compatActivity) {
        LayoutInflater inflater = compatActivity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_view,
                (ViewGroup) compatActivity.findViewById(R.id.custom_toast_container));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(signed_in_message);
        Toast toast = new Toast(compatActivity);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static boolean isFirstTimeLogin(Context context) {
        if (null == context) {
            return false;
        }
        SharedPreferences prefs = context.getSharedPreferences(FIRST_TIME_LOGIN, MODE_PRIVATE);
        Boolean isFirstTime = prefs.getBoolean(Constants.IS_FIRST_TIME, false);
        return isFirstTime;
    }

    public static void changeFirstTimeLogin(Context context) {
        if (null == context) {
            return;
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(FIRST_TIME_LOGIN, MODE_PRIVATE).edit();
        editor.putBoolean(Constants.IS_FIRST_TIME, false);
        editor.apply();
    }

    public static void updateWidget(Context context) {
        if (null != context) {
            Intent updateProgressIntent = new Intent(context, UpdateProgressIntentService.class);
            updateProgressIntent.setAction(UpdateProgressTasks.ACTION_SEND_DATA_TO_WIDGET);
            context.startService(updateProgressIntent);
        }
    }

}
