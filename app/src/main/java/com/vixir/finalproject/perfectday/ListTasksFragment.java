package com.vixir.finalproject.perfectday;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.thebluealliance.spectrum.SpectrumPalette;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ListTasksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ItemPickerDialogFragment.EditTaskDialogListener {
    private static final String TAG = ListTasksFragment.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;
    private View mView;
    private Paint p = new Paint();
    private int selectedColor = Color.WHITE;

    private ListTasksCursorAdapter mTasksCursorAdapter;

    @BindView(R.id.listtaskitems_recycler)
    RecyclerView mListTaskRecycler;

    @BindColor(R.color.very_light_gray)
    int veryLightGray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.list_task_fragment, container, false);
        ButterKnife.bind(this, mView);
        mListTaskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mTasksCursorAdapter = new ListTasksCursorAdapter(getActivity());
        mListTaskRecycler.setAdapter(mTasksCursorAdapter);
        mListTaskRecycler.setHasFixedSize(false);
        initSwipe();
        getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        ListTasksCursorAdapter.ViewHolderClick viewHolderClick = new ListTasksCursorAdapter.ViewHolderClick() {
            @Override
            public void onClick(int id) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                CalenderDialogFragment calenderDialogFragment = CalenderDialogFragment.newInstance(id);
                calenderDialogFragment.setTargetFragment(ListTasksFragment.this, 400);
                calenderDialogFragment.show(fm, "show_calender");
            }
        };
        mTasksCursorAdapter.setOnViewHolderClickListener(viewHolderClick);
        return mView;
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
                    return getActivity().getContentResolver().query(TaskItemsContract.TaskItemsColumns.CONTENT_URI,
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
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.title)
                            .content(R.string.content)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    int id = (int) viewHolder.itemView.getTag();
                                    String stringId = Integer.toString(id);
                                    Uri uri = TaskItemsContract.TaskItemsColumns.CONTENT_URI;
                                    uri = uri.buildUpon().appendPath(stringId).build();
                                    getActivity().getContentResolver().delete(uri, null, null);
                                    getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, ListTasksFragment.this);
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    mTasksCursorAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            })
                            .positiveText(R.string.agree)
                            .negativeText(R.string.disagree)
                            .show();

                } else {
                    int id = (int) viewHolder.itemView.getTag();
                    String stringId = Integer.toString(id);
                    Uri uri = TaskItemsContract.TaskItemsColumns.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(stringId).build();
                    Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

                    if (cursor.moveToNext()) {
                        int itemDescriptionIndex = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_DESCRIPTION);
                        int backColor = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COLOR);
                        DialogUtils.callEditDialog(getActivity(), ListTasksFragment.this, id, cursor.getString(itemDescriptionIndex), cursor.getInt(backColor));
                        mTasksCursorAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                }
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof TodayTasksCursorAdapter.ItemViewHolder) {
                    return ItemTouchHelper.LEFT;
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

                    if (dX < 0) {
                        p.setColor(veryLightGray);
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = drawableToBitmap(getResources().getDrawable(R.drawable.ic_rubbish_bin));
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(veryLightGray);
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = drawableToBitmap(getResources().getDrawable(R.drawable.ic_edit));
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mListTaskRecycler);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void onFinishedCreateDialog(String inputText, int color) {

    }

    @Override
    public void onFinishedEditDialog(int id, String inputText, int color) {
        Utils.editItem(getActivity(), id, inputText, color);
        getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }
}
