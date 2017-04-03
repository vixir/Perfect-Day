package com.vixir.finalproject.perfectday;

import android.content.Intent;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class UpdateProgressFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters params) {

        Intent updateProgressIntent = new Intent(this, UpdateProgressIntentService.class);
        updateProgressIntent.setAction(UpdateProgressTasks.ACTION_SEND_DATA_TO_WIDGET);
        this.startService(updateProgressIntent);

        updateProgressIntent.setAction(UpdateProgressTasks.ACTION_UPDATE_FIREBASE_DB);
        this.startService(updateProgressIntent);

        mBackgroundTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(params, false);
            }
        };

        mBackgroundTask.execute();
        return true;

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}
