package com.mrboomdev.platformer.game;

import android.content.Context;
import android.os.Bundle;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AsynchronousAndroidAudio;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.AudioUtil;

public class GameLauncher extends AndroidApplication {
	private FirebaseAnalytics analytics;
	private FirebaseCrashlytics crashlytics;
	private boolean isFinished;
	
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
		
		var settings = GameSettings.getFromSharedPreferences(prefs);
		settings.enableEditor = getIntent().getBooleanExtra("enableEditor", false);
		initialize(GameHolder.setInstance(this, settings, new GameAnalytics(analytics)));
		
		WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
		var windowController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
		windowController.hide(WindowInsetsCompat.Type.systemBars());
		windowController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
	}
	
	@Override
	public AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config) {
		return new AsynchronousAndroidAudio(context, config);
	}
	
	public void exit(Status status) {
		if(isFinished) return;
		AudioUtil.clear();
		switch(status) {
			case CRASH:
			case LOBBY:
				finish();
				break;
			case GAME_OVER:
				ActivityManager.gameOver();
				finish();
				break;
		}
		isFinished = true;
	}
	
	@Override
	public void onBackPressed() {}
	
	@Override
	public void onResume() {
		super.onResume();
		ActivityManager.current = this;
	}
	
	public enum Status {
		CRASH,
		GAME_OVER,
		LOBBY
	}
}