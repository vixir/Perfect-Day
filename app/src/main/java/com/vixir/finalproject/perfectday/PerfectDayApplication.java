package com.vixir.finalproject.perfectday;

import android.app.Application;

import com.facebook.stetho.Stetho;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class PerfectDayApplication extends Application{

        public void onCreate() {
            super.onCreate();
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()
            );
            Stetho.initializeWithDefaults(this);
        }
}
