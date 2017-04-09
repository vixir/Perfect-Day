package com.vixir.finalproject.perfectday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vixir.finalproject.perfectday.utils.UpdateProgressTasks;


public class UpdateDayAlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent updateProgressIntent = new Intent(context, UpdateProgressIntentService.class);
        updateProgressIntent.setAction(UpdateProgressTasks.ACTION_UPDATE_TODAY_INFORMATION);
        context.startService(updateProgressIntent);
    }
}
