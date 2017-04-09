package com.vixir.finalproject.perfectday.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.vixir.finalproject.perfectday.UpdateDayAlarmReciever;
import com.vixir.finalproject.perfectday.UpdateProgressFirebaseJobService;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class UpdateProgressUtilities {

    private static final int UPDATE_INTERVAL_MINUTES = 1;
    private static final int UPDATE_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(UPDATE_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = UPDATE_INTERVAL_SECONDS;

    private static final String UPDATE_PROGRESS_JOB_TAG = "update_progress_job_tag";

    private static boolean sInitialized;

    synchronized public static void scheduleUpdateProgressReminder(@NonNull final Context context) {
        if (sInitialized) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        Intent intentAlarm = new Intent(context, UpdateDayAlarmReciever.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getActualMaximum(Calendar.HOUR_OF_DAY), AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job constraintReminderJob = dispatcher.newJobBuilder()
                .setService(UpdateProgressFirebaseJobService.class)
                .setTag(UPDATE_PROGRESS_JOB_TAG)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        0,
                        UPDATE_INTERVAL_SECONDS))
                .setReplaceCurrent(true)
                .build();
        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(constraintReminderJob);
        /* The job has been initialized */
        sInitialized = true;
    }
}
