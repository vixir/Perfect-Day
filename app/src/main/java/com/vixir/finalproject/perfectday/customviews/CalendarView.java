package com.vixir.finalproject.perfectday.customviews;

import android.app.ActionBar;

import com.vixir.finalproject.perfectday.R;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ToggleButton;


public class CalendarView extends LinearLayout {

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    // default date format
    private static final String DATE_FORMAT = "MMM yyyy";

    private String dateFormat;

    private Calendar currentDate = Calendar.getInstance();
    private RecyclerView grid;

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    /**
     * Load control xml layout
     */
    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_calendar, this);
        loadDateFormat(attrs);
        assignUiElements();
        updateCalendar();
    }

    private void loadDateFormat(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try {
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        } finally {
            ta.recycle();
        }
    }

    private void assignUiElements() {
        grid = (RecyclerView) findViewById(R.id.calendar_grid);
    }

    public void updateCalendar() {
        updateCalendar(null);
    }

    public void updateCalendar(HashSet<Date> events) {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) currentDate.clone();

        new SimpleDateFormat("MMM").format(calendar.getTime());
        int actualMinimum = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        int monthDisplayIndex = calendar.getTime().getDate() - actualMinimum + 1;

        while (cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        CalendarAdapter calendarAdapter = new CalendarAdapter(getContext(), cells, events, monthDisplayIndex);
        grid.setAdapter(calendarAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        grid.setLayoutManager(layoutManager);
        calendarAdapter.notifyDataSetChanged();
        grid.setHasFixedSize(true);
    }


    private class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ItemViewHolder> {
        private HashSet<Date> eventDays;
        ArrayList<Date> days;
        private int monthDisplayIndex;
        private Context mContext;

        public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays, int monthDisplayIndex) {
            this.eventDays = eventDays;
            this.days = days;
            this.monthDisplayIndex = monthDisplayIndex;
            mContext = context;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.control_calendar_day, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, int position) {
            Date date = days.get(position);
            int day = date.getDate();
            int month = date.getMonth();
            int year = date.getYear();
            Date today = new Date();
            if (null != holder && (position == monthDisplayIndex || 0 == position)) {
                holder.monthName.setText(new SimpleDateFormat("MMM").format(date));
            } else {
                holder.monthName.setText("");
            }
            holder.calenderToggle.setChecked(false);
            holder.dayText.setBackgroundResource(0);
            holder.dayText.setText(String.valueOf(date.getDate()));
            if (eventDays != null) {
                for (Date eventDate : eventDays) {
                    if (eventDate.getDate() == day &&
                            eventDate.getMonth() == month &&
                            eventDate.getYear() == year) {
                        holder.dayText.setTextColor(Color.WHITE);
                        holder.dayText.setTypeface(null, Typeface.BOLD);
                        holder.calenderToggle.setChecked(true);
                        break;
                    }
                }
            }
//            if (month != today.getMonth() || year != today.getYear()) {
            holder.dayText.setTextColor(Color.WHITE);
            /*} else if (day == today.getDate()) {
              holder.dayText.setTypeface(null, Typeface.BOLD);
              }
            */

            // set text
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            if (null != days) {
                return days.size();
            } else {
                return 0;
            }
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView dayText;
            TextView monthName;
            ToggleButton calenderToggle;

            public ItemViewHolder(View itemView) {
                super(itemView);
                dayText = (TextView) itemView.findViewById(R.id.day_text);
                monthName = (TextView) itemView.findViewById(R.id.month_name);
                calenderToggle = (ToggleButton) itemView.findViewById(R.id.toggle_calender);
            }
        }

    }

}