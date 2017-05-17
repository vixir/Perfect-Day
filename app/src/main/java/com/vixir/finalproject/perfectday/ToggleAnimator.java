package com.vixir.finalproject.perfectday;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.vixir.finalproject.perfectday.adapters.TodayTasksCursorAdapter;

import java.util.List;

public class ToggleAnimator extends DefaultItemAnimator {

    @Override
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {
        StateInfo info = (StateInfo) super.recordPreLayoutInformation(state, (TodayTasksCursorAdapter.ItemViewHolder) viewHolder, changeFlags, payloads);
        info.toggleImg = ((TodayTasksCursorAdapter.ItemViewHolder) viewHolder).mButton;
        return info;
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder) {
        StateInfo info = (StateInfo) super.recordPostLayoutInformation(state, viewHolder);
        info.toggleImg = ((TodayTasksCursorAdapter.ItemViewHolder) viewHolder).mButton;
        return info;
    }

    @Override
    public ItemHolderInfo obtainHolderInfo() {
        return new StateInfo();
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo) {
        if (oldHolder != newHolder) {
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
        }
        TodayTasksCursorAdapter.ItemViewHolder holder = (TodayTasksCursorAdapter.ItemViewHolder) newHolder;

        boolean isFinished = holder.isFinished;
        if (isFinished) {
            final int[] stateSet = {android.R.attr.state_checked * (-1)};
            holder.mButton.setImageState(stateSet, false);
        } else {
            final int[] stateSet = {android.R.attr.state_checked * (1)};
            holder.mButton.setImageState(stateSet, true);
        }
        return true;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        super.endAnimation(item);
    }

    @Override
    public boolean isRunning() {
        return super.isRunning();
    }

    @Override
    public void endAnimations() {
        super.endAnimations();
    }

    private class StateInfo extends ItemHolderInfo {
        ImageView toggleImg;
        boolean isSelected;
    }
}
