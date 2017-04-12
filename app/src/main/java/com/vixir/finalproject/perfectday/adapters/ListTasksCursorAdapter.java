package com.vixir.finalproject.perfectday.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.vixir.finalproject.perfectday.R;
import com.vixir.finalproject.perfectday.db.TaskItemsContract;
import com.vixir.finalproject.perfectday.utils.Utils;


public class ListTasksCursorAdapter extends RecyclerView.Adapter<ListTasksCursorAdapter.ItemViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private ViewHolderClick viewHolderClick;

    public ListTasksCursorAdapter(Context context) {
        this.mContext = context;
    }

    public interface ViewHolderClick {
        void onClick(int id);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_task_item, parent, false);
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
        int isTodayIndex = mCursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY);
        mCursor.moveToPosition(position);
        final int id = mCursor.getInt(idIndex);
        holder.itemView.setTag(id);
        String description = mCursor.getString(itemDescriptionIndex);
        final int color = mCursor.getInt(backColor);
        final int isToday = mCursor.getInt(isTodayIndex);
        holder.mDescriptionTextView.setText(description);
        holder.mDescriptionTextView.setTextColor(color);
        holder.mMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolderClick.onClick(id);
            }
        });
        final ToggleButton mButton = holder.mButton;
        if (isToday == 1) {
            mButton.setChecked(true);
            mButton.setVisibility(View.INVISIBLE);
        } else {
            mButton.setChecked(false);
            mButton.setVisibility(View.INVISIBLE);
            final Drawable drawable = mButton.getBackground();
            new AsyncTask<Drawable, Void, Drawable>() {
                @Override
                protected Drawable doInBackground(Drawable... params) {
                    Drawable local = DrawableCompat.wrap(drawable);
                    local = local.mutate();
                    DrawableCompat.setTint(local, color);
                    return local;
                }

                @Override
                protected void onPostExecute(Drawable drawable) {
                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP);
                    super.onPostExecute(drawable);
                    mButton.setVisibility(View.VISIBLE);
                }
            }.execute(drawable);

        }
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isToday == 1) {
                    return;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY, 1);
                Uri uri = TaskItemsContract.TaskItemsColumns.CONTENT_URI;
                uri = uri.buildUpon().appendPath(String.valueOf(id)).build();
                mContext.getContentResolver().update(uri, contentValues, null, null);
                Utils.showWhiteSnackBar(R.string.moved_today_message, (AppCompatActivity) mContext);
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

    public void setOnViewHolderClickListener(ViewHolderClick viewHolderClick) {
        this.viewHolderClick = viewHolderClick;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mDescriptionTextView;
        View mMainView;
        ToggleButton mButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.task_list_item_description);
            Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Montserrat-Regular.ttf");
            mDescriptionTextView.setTypeface(custom_font);
            mButton = (ToggleButton) itemView.findViewById(R.id.list_add_today);
            mMainView = itemView;
        }

    }
}
