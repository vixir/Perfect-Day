package com.vixir.finalproject.perfectday.application;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.vixir.finalproject.perfectday.R;

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
