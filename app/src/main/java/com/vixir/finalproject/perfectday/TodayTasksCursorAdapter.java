package com.vixir.finalproject.perfectday;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import butterknife.BindView;


public class TodayTasksCursorAdapter extends RecyclerView.Adapter<TodayTasksCursorAdapter.ItemViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public TodayTasksCursorAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.task_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        int idIndex = mCursor.getColumnIndex(TaskItemsContract.TaskItemsColumns._ID);
        int itemDescriptionIndex = mCursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_DESCRIPTION);
        int backColor = mCursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COLOR);
        int isFinishedIndex = mCursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED);
        mCursor.moveToPosition(position);
        final int id = mCursor.getInt(idIndex);
        holder.itemView.setTag(id);
        String description = mCursor.getString(itemDescriptionIndex);
        final int color = mCursor.getInt(backColor);
        final int isFinished = mCursor.getInt(isFinishedIndex);
        holder.mDescriptionTextView.setText(description);
        holder.mDescriptionTextView.setTextColor(color);
        ToggleButton mButton = holder.mButton;
        if (isFinished == 0) {
            mButton.setChecked(false);
            Drawable drawable = mButton.getBackground();
            drawable = DrawableCompat.wrap(drawable);
            drawable = drawable.mutate();
            DrawableCompat.setTint(drawable, color);
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP);
        } else {
            mButton.setChecked(true);
            Drawable drawable = mButton.getBackground();
            drawable = DrawableCompat.wrap(drawable);
            drawable = drawable.mutate();
            DrawableCompat.setTint(drawable, color);
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP);

        }
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                if (isFinished == 0) {
                    contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED, 1);
                } else {
                    contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED, 0);
                }
                Uri uri = TaskItemsContract.TaskItemsColumns.CONTENT_URI;
                uri = uri.buildUpon().appendPath(String.valueOf(id)).build();
                mContext.getContentResolver().update(uri, contentValues, null, null);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mDescriptionTextView;
        View mMainView;
        ToggleButton mButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.list_item_description);
            mButton = (ToggleButton) itemView.findViewById(R.id.check_complete);
            mMainView = itemView;
        }
    }
}
