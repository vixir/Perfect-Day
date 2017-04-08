package com.vixir.finalproject.perfectday;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.vixir.finalproject.perfectday.ItemPickerDialogFragment.EDIT_ITEM;
import static com.vixir.finalproject.perfectday.ItemPickerDialogFragment.NEW_ITEM;


public class TodayTasksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ItemPickerDialogFragment.EditTaskDialogListener {
    private static final String TAG = TodayTasksFragment.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;
    private View mView;
    private Paint p = new Paint();

    private TodayTasksCursorAdapter mTasksCursorAdapter;

    @BindView(R.id.taskitems_recycler)
    RecyclerView mTodayTaskRecycler;

    @BindColor(R.color.very_light_gray)
    int veryLightGray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.todays_task_fragment, container, false);
        ButterKnife.bind(this, mView);
        mTodayTaskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mTasksCursorAdapter = new TodayTasksCursorAdapter(getActivity());
        mTodayTaskRecycler.setAdapter(mTasksCursorAdapter);
        mTodayTaskRecycler.setHasFixedSize(false);
        initSwipe();
        getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        return mView;
    }

    @OnClick(R.id.add_item_button)
    public void showHide() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ItemPickerDialogFragment pickTaskName = ItemPickerDialogFragment.newInstance(0, "", 0);
        pickTaskName.setTargetFragment(TodayTasksFragment.this, NEW_ITEM);
        pickTaskName.show(fm, "add_item");
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(getActivity()) {

            Cursor mTaskDAta = null;

            @Override
            protected void onStartLoading() {
                if (mTaskDAta != null) {
                    notifyLoaderChangeListener();
                } else {
                    forceLoad();
                }
            }

            private void notifyLoaderChangeListener() {

            }

            @Override
            public Cursor loadInBackground() {
                //to do Make it private again
                try {
                    Uri todaysUri = TaskItemsContract.TaskItemsColumns.CONTENT_URI.buildUpon().appendPath(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY).appendPath("1").build();
                    return getActivity().getContentResolver().query(todaysUri,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTasksCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTasksCursorAdapter.swapCursor(null);
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //run in background thread
                if (direction == ItemTouchHelper.LEFT) {
                    int id = (int) viewHolder.itemView.getTag();
                    Uri uri = TaskItemsContract.TaskItemsColumns.CONTENT_URI;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY, 0);
                    contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED, 0);
                    uri = uri.buildUpon().appendPath(String.valueOf(id)).build();
                    getActivity().getContentResolver().update(uri, contentValues, null, null);
                    getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, TodayTasksFragment.this);
                } else {
                    int id = (int) viewHolder.itemView.getTag();
                    String stringId = Integer.toString(id);
                    Uri uri = TaskItemsContract.TaskItemsColumns.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(stringId).build();
                    Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

                    if (cursor.moveToNext()) {
                        int itemDescriptionIndex = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_DESCRIPTION);
                        int backColor = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COLOR);
                        callEditDialog(id, cursor.getString(itemDescriptionIndex), cursor.getInt(backColor));
                        mTasksCursorAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                }
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof TodayTasksCursorAdapter.ItemViewHolder) {
                    return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(veryLightGray);
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = drawableToBitmap(getResources().getDrawable(R.drawable.ic_edit));
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(veryLightGray);
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = drawableToBitmap(getResources().getDrawable(R.drawable.ic_rubbish_bin));
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mTodayTaskRecycler);
    }

    private void callEditDialog(int id, String description, int color) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ItemPickerDialogFragment pickTaskName = ItemPickerDialogFragment.newInstance(id, description, color);
        pickTaskName.setTargetFragment(TodayTasksFragment.this, EDIT_ITEM);
        pickTaskName.show(fm, "edit_item");
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

    @Override
    public void onFinishedCreateDialog(String inputText, int color) {
        addItem(inputText, color);
    }

    @Override
    public void onFinishedEditDialog(int id, String inputText, int color) {
        editItem(id, inputText, color);
        getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    private void editItem(int id, String inputText, int color) {
        Uri uri = TaskItemsContract.TaskItemsColumns.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COLOR, color);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_DESCRIPTION, inputText);
        uri = uri.buildUpon().appendPath(String.valueOf(id)).build();
        getActivity().getContentResolver().update(uri, contentValues, null, null);
    }

    private void addItem(String description, int color) {
        if (description.length() == 0) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_DESCRIPTION, description);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED, 0);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY, 1);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COLOR, color);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_CREATED_AT, System.currentTimeMillis());
        Uri uri = getActivity().getContentResolver().insert(TaskItemsContract.TaskItemsColumns.CONTENT_URI, contentValues);
        if (uri != null) {
            Toast.makeText(getContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
        getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

}
