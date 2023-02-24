package com.mrboomdev.platformer.game;

import android.content.SharedPreferences;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mrboomdev.platformer.BuildConfig;

public class GameLauncher extends AndroidApplication {
	private FirebaseAnalytics analytics;
	private FirebaseCrashlytics crashlytics;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		analytics = FirebaseAnalytics.getInstance(this);
		crashlytics = FirebaseCrashlytics.getInstance();
		
		var prefs = getSharedPreferences("Save", 0);
		if(!prefs.getBoolean("crashlytics", true) || BuildConfig.DEBUG) {
        	crashlytics.setCrashlyticsCollectionEnabled(false);
		}
		
		var config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
        config.useAccelerometer = false;
        config.useCompass = false;
		
		initialize(GameHolder.setInstance(this,
			GameSettings.getFromSharedPreferences(prefs),
			new GameAnalytics(analytics)));
	}
	
	public void exit() {
		finish();
	}
}