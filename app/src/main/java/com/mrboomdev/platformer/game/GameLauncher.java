package com.mrboomdev.platformer.game;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowInsets;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AsynchronousAndroidAudio;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.react.ReactGameOverActivity;
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
		getWindow().getInsetsController().hide(WindowInsets.Type.navigationBars());
	}
	
	@Override
	public AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config) {
		return new AsynchronousAndroidAudio(context, config);
	}
	
	public void exit() {
		if(isFinished) return;
		AudioUtil.clear();
		Intent intent = new Intent(this, ReactGameOverActivity.class);
		startActivity(intent);
		isFinished = true;
		finish();
	}
	
	@Override
	public void onBackPressed() {}
}