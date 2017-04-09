package com.vixir.finalproject.perfectday;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.vixir.finalproject.perfectday.utils.UpdateProgressTasks;


public class UpdateProgressIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UpdateProgressIntentService() {
        super("UpdateProgressIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        UpdateProgressTasks.executeTask(this, action);
    }
}
