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
    public void logDebug(String name, String content) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(name);
        builder.append("]");
        Log.d("[DebugLog]" + builder.toString(), content);
        crashlytics.log(builder.toString() + " " + content);
    }
}
