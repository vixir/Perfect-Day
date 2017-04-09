package com.vixir.finalproject.perfectday;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.style.StrikethroughSpan;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.vixir.finalproject.perfectday.db.TaskItemsContract;
import static android.os.Binder.clearCallingIdentity;
import static android.os.Binder.restoreCallingIdentity;

public class TodayWidgetService extends RemoteViewsService {

    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new ListViewRemoteViewsFactory(this.getApplicationContext(), intent);

    }


    class ListViewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context mContext;

        private Cursor mCursor;

        public ListViewRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
        }

        // Initialize the data set.

        public void onCreate() {



        }

        // Given the position (index) of a WidgetItem in the array, use the item's text value in
        // combination with the app widget item XML file to construct a RemoteViews object.
        public RemoteViews getViewAt(int position) {

            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
            if (!mCursor.moveToPosition(position)) {
                return null;
            }
            int idIndex = mCursor.getColumnIndex(TaskItemsContract.TaskItemsColumns._ID);
            int itemDescriptionIndex = mCursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_DESCRIPTION);
            int backColor = mCursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COLOR);
            int isFinishedIndex = mCursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED);
            int id = mCursor.getInt(idIndex);
            String description = mCursor.getString(itemDescriptionIndex);
            int color = mCursor.getInt(backColor);
            int isFinished = mCursor.getInt(isFinishedIndex);
            rv.setTextColor(R.id.item, color);
            final StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();
            if (isFinished == 1) {
                rv.setTextViewText(R.id.item, description + isFinished);
            } else {
                rv.setTextViewText(R.id.item, description + isFinished);
            }
            Bundle extras = new Bundle();
            extras.putInt(TodayWidgetProvider.EXTRA_ITEM, position);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            rv.setOnClickFillInIntent(R.id.item, fillInIntent);
            return rv;

        }

        public int getCount() {
            if (null != mCursor)
                return mCursor.getCount();
            else return 0;
        }

        public void onDataSetChanged() {
            if (mCursor != null) {
                mCursor.close();
            }
            final long identityToken = clearCallingIdentity();;
            mCursor = mContext.getContentResolver().query(TaskItemsContract.TaskItemsColumns.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            restoreCallingIdentity(identityToken);
        }

        public int getViewTypeCount() {

            return 1;

        }

        public long getItemId(int position) {

            return position;

        }

        public void onDestroy() {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        }

        public boolean hasStableIds() {

            return true;

        }

        public RemoteViews getLoadingView() {

            return null;

        }

    }

}
