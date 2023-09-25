package com.mrboomdev.platformer.game;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mrboomdev.binacty.rn.RNApp;
import com.mrboomdev.platformer.BuildConfig;

public class GameAnalytics {
	private static GameAnalytics instance;
	private final FirebaseAnalytics firebaseAnalytics;
	
	private GameAnalytics(FirebaseAnalytics firebaseAnalytics) {
		this.firebaseAnalytics = firebaseAnalytics;
	}

	public static GameAnalytics setInstance(FirebaseAnalytics fb) {
		instance = new GameAnalytics(fb);
		return instance;
	}

	public static GameAnalytics getInstance() {
		return instance;
	}
	
	public void log(String title, String message) {
		this.logMessage(LogLevel.DEBUG, title, message);
	}
	
	public void error(String title, String message) {
		this.logMessage(LogLevel.ERROR, title, message);
	}

	public void crash(String title, String message, Throwable throwable) {
		this.logMessage(LogLevel.ERROR, title, message);

		if(isCrashlyticsDisabled()) return;
		var crashlytics = FirebaseCrashlytics.getInstance();
		crashlytics.recordException(throwable);
		crashlytics.sendUnsentReports();
	}
	
	private void logMessage(LogLevel level, String title, String message) {
		if(level == LogLevel.ERROR) {
			Log.e(title, message);
		} else {
			Log.d(title, message);
		}

		if(isCrashlyticsDisabled()) return;
		Bundle bundle = new Bundle();
		bundle.putString("Level", level == LogLevel.ERROR ? "Error" : "Debug");
		bundle.putString("Message", message);
		firebaseAnalytics.logEvent(title, bundle);
	}

	private boolean isCrashlyticsDisabled() {
		return BuildConfig.DEBUG || !RNApp.getSave("Save").getBoolean("crashlytics", true);
	}
	
	public enum LogLevel {
		ERROR,
		DEBUG
	}
}