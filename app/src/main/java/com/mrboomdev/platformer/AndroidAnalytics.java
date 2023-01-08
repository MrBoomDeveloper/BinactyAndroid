package com.mrboomdev.platformer;

import android.util.Log;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mrboomdev.platformer.util.Analytics;

public class AndroidAnalytics implements Analytics {
    private FirebaseCrashlytics crashlytics;
    
    public AndroidAnalytics() {
        this.crashlytics = FirebaseCrashlytics.getInstance();
    }

    @Override
    public void logDebug(String tag, String content) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(tag);
        builder.append("]");
        Log.d("[DebugLog]" + builder.toString(), content);
        crashlytics.log(builder.toString() + " " + content);
    }
    
    @Override
    public void logInfo(String tag, String content) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(tag);
        builder.append("]");
        Log.i("[InfoLog]" + builder.toString(), content);
        crashlytics.log(builder.toString() + " " + content);
    }
}