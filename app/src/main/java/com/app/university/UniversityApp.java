package com.app.university;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by matt on 2015/2/25.
 */
public class UniversityApp extends Application {
    public static Context context;


    private static final String GOOGLE_ANALYSIS_TRACKING_ID = "UA-60061288-1";
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
    }
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public UniversityApp() {
        super();
    }

    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(GOOGLE_ANALYSIS_TRACKING_ID);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
}
