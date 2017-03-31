package com.vixir.finalproject.perfectday;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        int idIndex = mCursor.getColumnIndex(TaskItemsContract.TaskItemsColumns._ID);
        int itemDescriptionIndex = mCursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_DESCRIPTION);
        int backColor = mCursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COLOR);
        mCursor.moveToPosition(position);
        final int id = mCursor.getInt(idIndex);
        String description = mCursor.getString(itemDescriptionIndex);
        String color = mCursor.getString(backColor);
        holder.mDescriptionTextView.setText(description);
        holder.mDescriptionTextView.setBackgroundColor(Color.parseColor(color));

    }

    private int getColor(int backColor) {
        //return with alpha
        return 0;
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

        public ItemViewHolder(View itemView) {
            super(itemView);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.list_item_description);
            mMainView = itemView;
        }
    }
}
