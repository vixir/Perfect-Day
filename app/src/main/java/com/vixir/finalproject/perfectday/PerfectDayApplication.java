package com.vixir.finalproject.perfectday;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class PerfectDayApplication extends Application{

        public void onCreate() {
            super.onCreate();
            Stetho.initializeWithDefaults(this);
        }
}
