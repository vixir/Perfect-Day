package com.vixir.finalproject.perfectday;

import android.content.Intent;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.vixir.finalproject.perfectday.utils.UpdateProgressTasks;

public class UpdateProgressFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters params) {

        Intent updateProgressIntent = new Intent(this, UpdateProgressIntentService.class);
        updateProgressIntent.setAction(UpdateProgressTasks.ACTION_UPDATE_FIREBASE_DB);
        this.startService(updateProgressIntent);

        return true;

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}
