package com.vixir.finalproject.perfectday.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.vixir.finalproject.perfectday.ToggleAnimator;
import com.vixir.finalproject.perfectday.activities.CreateTaskBySpeech;
import com.vixir.finalproject.perfectday.utils.DialogUtils;
import com.vixir.finalproject.perfectday.R;
import com.vixir.finalproject.perfectday.db.TaskItemsContract;
import com.vixir.finalproject.perfectday.adapters.TodayTasksCursorAdapter;
import com.vixir.finalproject.perfectday.utils.Utils;
import com.vixir.finalproject.perfectday.customdialogs.ItemPickerDialogFragment;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.toptas.fancyshowcase.FancyShowCaseView;

import static com.vixir.finalproject.perfectday.customdialogs.ItemPickerDialogFragment.NEW_ITEM;


public class TodayTasksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ItemPickerDialogFragment.EditTaskDialogListener {
    private static final String TAG = TodayTasksFragment.class.getSimpleName();
    private View mView;
    private Paint p = new Paint();
    private static final int TASK_LOADER_ID = 0;
    private static boolean mIsShowcased = false;

    private TodayTasksCursorAdapter mTasksCursorAdapter;

    private ToggleAnimator mToggleAnimator = new ToggleAnimator();
    private DefaultItemAnimator mDefaultAnimator = new DefaultItemAnimator();

    @BindView(R.id.taskitems_recycler)
    RecyclerView mTodayTaskRecycler;

    @BindView((R.id.add_item_button))
    protected ImageView mAddButton;


    @BindColor(R.color.transparent_red)
    protected int mHintColor;

    @BindColor(R.color.very_light_gray)
    int veryLightGray;

    @BindView(R.id.streak_title)
    TextView streakTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.todays_task_fragment, container, false);
        ButterKnife.bind(this, mView);
        mTodayTaskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mTodayTaskRecycler.setItemAnimator(mToggleAnimator);
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

    @OnClick(R.id.add_by_voice_button)
    public void addItemByVoice() {
        Intent intent = new Intent(getActivity(), CreateTaskBySpeech.class);
        startActivity(intent);
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
                try {
                    Uri todayUri = TaskItemsContract.TaskItemsColumns.CONTENT_URI.buildUpon().appendPath(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY).appendPath("1").build();
                    return getActivity().getContentResolver().query(todayUri,
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
        if (null != data && data.getCount() > 0) {
            streakTitle.setVisibility(View.VISIBLE);
            if (null != getActivity() && Utils.isFirstTimeLogin(getActivity()) && mIsShowcased == false) {
                new FancyShowCaseView.Builder(getActivity())
                        .focusOn(streakTitle)
                        .backgroundColor(mHintColor)
                        .title(getString(R.string.hint_edit_task))
                        .titleStyle(0, Gravity.CENTER)
                        .build()
                        .show();
                mIsShowcased = true;
            }
        } else {
            streakTitle.setVisibility(View.INVISIBLE);
        }
        if (null != getActivity() && Utils.isFirstTimeLogin(getActivity()) && mIsShowcased == false) {
            new FancyShowCaseView.Builder(getActivity())
                    .focusOn(mAddButton)
                    .roundRectRadius(20)
                    .focusCircleRadiusFactor(2.5)
                    .backgroundColor(mHintColor)
                    .title(getString(R.string.hint_create_item))
                    .titleStyle(0, Gravity.CENTER)
                    .build()
                    .show();
        }
        if (mTasksCursorAdapter == null) {
            mTasksCursorAdapter = new TodayTasksCursorAdapter(getActivity(), this);
            mTodayTaskRecycler.setAdapter(mTasksCursorAdapter);
        }
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
                //run in background thread
                if (direction == ItemTouchHelper.LEFT) {
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.title_archive)
                            .content(R.string.archive_content)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    int id = (int) viewHolder.itemView.getTag();
                                    Uri uri = TaskItemsContract.TaskItemsColumns.CONTENT_URI;
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY, 0);
                                    contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED, 0);
                                    uri = uri.buildUpon().appendPath(String.valueOf(id)).build();
                                    getActivity().getContentResolver().update(uri, contentValues, null, null);
                                    getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, TodayTasksFragment.this);
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                    mTasksCursorAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                    // Till I learn how to do that left to right animation.
                                    mTasksCursorAdapter.notifyDataSetChanged();
                                }
                            }).canceledOnTouchOutside(false)
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
                        DialogUtils.callEditDialog(getActivity(), TodayTasksFragment.this, id, cursor.getString(itemDescriptionIndex), cursor.getInt(backColor));
//                        mTasksCursorAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        mTasksCursorAdapter.notifyDataSetChanged();

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
                        icon = Utils.drawableToBitmap(getResources().getDrawable(R.drawable.ic_edit));
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(veryLightGray);
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = Utils.drawableToBitmap(getResources().getDrawable(R.drawable.ic_archive));
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


    @Override
    public void onFinishedCreateDialog(String inputText, int color) {
        if (null != getActivity()) {
            Utils.addItem(getActivity(), inputText, color);
            getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
            Utils.updateWidget(getActivity());
        }
    }

    @Override
    public void onFinishedEditDialog(int id, String inputText, int color) {
        if (null != getActivity()) {
            Utils.editItem(getActivity(), id, inputText, color);
            getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
        }
    }


}
