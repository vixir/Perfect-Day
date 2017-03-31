package com.vixir.finalproject.perfectday;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class TodayTasksAdapter extends RecyclerView.Adapter<TodayTasksAdapter.ItemViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public TodayTasksAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.task_item, parent, false);
        return null;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
            if(!mCursor.moveToPosition(position)){
                return;
            }
            holder.mDescriptionTextView.setText(position);

    }

    @Override
    public int getItemCount() {
        return 0;
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
