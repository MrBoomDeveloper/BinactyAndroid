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
        Log.d(generateTag("DEBUG", tag, true), content);
        crashlytics.log(generateTag("DEBUG", tag, false) + content);
    }
    
    @Override
    public void logInfo(String tag, String content) {
        Log.d(generateTag("INFO", tag, true), content);
        crashlytics.log(generateTag("INFO", tag, false) + content);
    }
    
    @Override
    public void logError(String tag, String content) {
        Log.d(generateTag("ERROR", tag, true), content);
        crashlytics.log(generateTag("ERROR", tag, false) + content);
    }
    
    private String generateTag(String type, String tag, boolean isLogcat) {
        if(isLogcat) return "_ACTION_" + type + "_" + tag;
        return "[" + type + "_" + tag + "] ";
    }
}