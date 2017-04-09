package com.vixir.finalproject.perfectday;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalenderDialogFragment extends BottomSheetDialogFragment {


    @BindView(R.id.calender_view)
    protected CalendarView mCalendarView;

    private View mView;
    private static int itemId;

    public static CalenderDialogFragment newInstance(int id) {
        CalenderDialogFragment calenderDialogFragment = new CalenderDialogFragment();
        Bundle args = new Bundle();
        itemId = id;
        calenderDialogFragment.setArguments(args);
        return calenderDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = getActivity().getLayoutInflater().inflate(R.layout.calender_dialog_fragment, container);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Uri uri = TaskItemsContract.TaskItemsColumns.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(itemId)).build();
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToNext()) {
            int itemDescriptionIndex = cursor.getColumnIndex(TaskItemsContract.TaskItemsColumns.COLUMN_NAME_COMPLETED_DATES);
            String dates = cursor.getString(itemDescriptionIndex);
            try {
                JSONObject jsonObject = new JSONObject(dates);
                JSONArray jsonArray = jsonObject.optJSONArray(UpdateProgressTasks.UNIQUE_DAYS_KEY);
                HashSet<Date> events = new HashSet<Date>();
                if (null != jsonArray) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        events.add(new Date(jsonArray.getLong(i)));
                    }
                }
                mCalendarView.updateCalendar(events);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        super.onViewCreated(view, savedInstanceState);
    }
}
