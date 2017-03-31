package com.vixir.finalproject.perfectday;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TodayTasksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = TodayTasksFragment.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;
    private View mView;

    @BindView(R.id.task_description_input)
    protected EditText mDescriptionInput;

    private TodayTasksCursorAdapter mTasksCursorAdapter;

    @BindView(R.id.create_ok_button)
    Button mAddItemButton;

    @BindView(R.id.taskitems_recycler)
    RecyclerView mTodayTaskRecycler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.todays_task_fragment, container, false);
        ButterKnife.bind(this, mView);
        mTodayTaskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mTasksCursorAdapter = new TodayTasksCursorAdapter(getActivity());
        mTodayTaskRecycler.setAdapter(mTasksCursorAdapter);
        mTodayTaskRecycler.setHasFixedSize(false);
        getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        return mView;
    }

    @OnClick(R.id.create_ok_button)
    public void addItem() {
        String description = mDescriptionInput.getText().toString();
        if (description.length() == 0) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_DESCRIPTION, description);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_FINISHED, 0);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_IS_TODAY, 1);
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COLOR, "#123543");
        contentValues.put(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_CREATED_AT, System.currentTimeMillis());

        Uri uri = getActivity().getContentResolver().insert(TaskItemsContract.TaskItemsColumns.CONTENT_URI, contentValues);
        if (uri != null) {
            Toast.makeText(getContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
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
}
