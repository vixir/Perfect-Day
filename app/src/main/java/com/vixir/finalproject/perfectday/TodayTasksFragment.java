package com.vixir.finalproject.perfectday;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class TodayTasksFragment extends Fragment {

    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.todays_task_fragment, container, false);
        ButterKnife.bind(getActivity());
        setHasOptionsMenu(true);
        return mView;
    }

    @OnClick(R.id.add_item_button)
    public void addItem() {

    }

}
