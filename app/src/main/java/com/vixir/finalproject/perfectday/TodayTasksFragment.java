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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.thebluealliance.spectrum.SpectrumPalette;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TodayTasksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = TodayTasksFragment.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;
    private View mView;
    private Paint p = new Paint();
    private int selectedColor = Color.WHITE;
    @BindColor(R.color.picker_red)
    int pickerRed;
    @BindColor(R.color.picker_amber)
    int pickerAmber;
    @BindColor(R.color.picker_teal)
    int pickerTeal;
    @BindColor(R.color.picker_blue)
    int pickerBlue;
    @BindColor(R.color.picker_orange)
    int pickerOrange;
    @BindColor(R.color.picker_yellow)
    int pickerYellow;

    @BindView(R.id.task_description_input)
    protected EditText mDescriptionInput;

    private TodayTasksCursorAdapter mTasksCursorAdapter;

    @BindView(R.id.create_ok_button)
    Button mAddItemButton;

    @BindView(R.id.taskitems_recycler)
    RecyclerView mTodayTaskRecycler;

    @BindView(R.id.picker_view)
    View showHide;

    @BindView(R.id.color_picker)
    SpectrumPalette mSpectrumPalette;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.todays_task_fragment, container, false);
        ButterKnife.bind(this, mView);
        mTodayTaskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mTasksCursorAdapter = new TodayTasksCursorAdapter(getActivity());
        mTodayTaskRecycler.setAdapter(mTasksCursorAdapter);
        mTodayTaskRecycler.setHasFixedSize(false);
        mSpectrumPalette.setColors(new int[]{pickerAmber, pickerBlue, pickerOrange, pickerRed, pickerTeal, pickerYellow});
        mSpectrumPalette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
            @Override
            public void onColorSelected(@ColorInt int color) {
                selectedColor = color;
                mDescriptionInput.setTextColor(color);
            }
        });
        mSpectrumPalette.setSelectedColor(Color.WHITE);
        initSwipe();
        getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        return mView;
    }

    @OnClick(R.id.add_item_button)
    public void showHide() {
        if (showHide.getVisibility() == View.GONE) {
            mDescriptionInput.setText("");
            showHide.setVisibility(View.VISIBLE);
        } else {
            showHide.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.create_ok_button)
    public void addItem() {
        String description = mDescriptionInput.getText().toString();
        if (description.length() == 0) {
            return;
        }
        //To DO only numbers and letters
        showHide();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_DESCRIPTION, description);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED, 0);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY, 1);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COLOR, selectedColor);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_CREATED_AT, System.currentTimeMillis());
        Uri uri = getActivity().getContentResolver().insert(TaskItemsContract.TaskItemsColumns.CONTENT_URI, contentValues);
        if (uri != null) {
            Toast.makeText(getContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
        getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
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
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    int id = (int) viewHolder.itemView.getTag();
                    String stringId = Integer.toString(id);
                    Uri uri = TaskItemsContract.TaskItemsColumns.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(stringId).build();
                    getActivity().getContentResolver().delete(uri, null, null);
                    getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, TodayTasksFragment.this);
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
                        p.setColor(Color.parseColor("#d32f2f"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = drawableToBitmap(getResources().getDrawable(R.drawable.ic_delete_white_24px));
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
}
