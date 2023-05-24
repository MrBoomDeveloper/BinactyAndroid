package com.mrboomdev.platformer.game;

import android.os.Bundle;
import android.util.Log;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class GameAnalytics {
	private final FirebaseAnalytics firebaseAnalytics;
	
	public GameAnalytics(FirebaseAnalytics firebaseAnalytics) {
		this.firebaseAnalytics = firebaseAnalytics;
	}
	
	public void log(String title, String message) {
		this.logMessage(LogLevel.DEBUG, title, message);
	}
	
	public void error(String title, String message) {
		this.logMessage(LogLevel.ERROR, title, message);
	}

	public void crash(String title, String message, Throwable throwable) {
		this.logMessage(LogLevel.ERROR, title, message);
		var crashlytics = FirebaseCrashlytics.getInstance();
		crashlytics.recordException(throwable);
		crashlytics.sendUnsentReports();
	}
	
	private void logMessage(LogLevel level, String title, String message) {
		Bundle bundle = new Bundle();
		bundle.putString("Level", level == LogLevel.ERROR ? "Error" : "Debug");
		bundle.putString("Message", message);
		firebaseAnalytics.logEvent(title, bundle);
		if(level == LogLevel.ERROR) {
			Log.e(title, message);
		} else {
			Log.d(title, message);
		}
	}
	
	public enum LogLevel {
		ERROR,
		DEBUG
	}
}