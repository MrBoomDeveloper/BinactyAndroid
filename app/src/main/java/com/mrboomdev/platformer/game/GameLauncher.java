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
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.AudioUtil;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class GameLauncher extends AndroidApplication {
	private GameHolder game;
	private boolean isFinished;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
		FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
		
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
		settings.ignoreScriptErrors = true;
		initialize(GameHolder.setInstance(this, settings, new GameAnalytics(analytics)));
		game = GameHolder.getInstance();
		try {
			resolveGameFiles();
		} catch(Exception e) {
			e.printStackTrace();
			if(!BuildConfig.DEBUG) exit(Status.CRASH);
		}
		
		WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
		var windowController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
		windowController.hide(WindowInsetsCompat.Type.systemBars());
		windowController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
	}

	private void resolveGameFiles() throws IOException {
		if(!getIntent().hasExtra("gamemodeFile")) return;
		Moshi moshi = new Moshi.Builder().build();
		JsonAdapter<FileUtil> adapter = moshi.adapter(FileUtil.class);
		game.gamemodeFile = adapter.fromJson(getIntent().getCharSequenceExtra("gamemodeFile").toString());
		game.mapFile = adapter.fromJson(getIntent().getCharSequenceExtra("mapFile").toString());
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
				ActivityManager.forceExit();
				finish();
				break;
			case GAME_OVER:
				ActivityManager.gameOver();
				finish();
				break;
		}
		isFinished = true;
	}
	
	public void pause() {
		game.settings.pause = true;
		AudioUtil.pause();

		var dialog = new AndroidDialog().setTitle("Game Paused").setCancelable(false);
		dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT).setTextColor("#ffffff").setText("So you've just stopped the entire universe, huh?"));
		dialog.addAction(new AndroidDialog.Action().setText("Exit").setClickListener(button -> {
			game.settings.pause = false;
			game.launcher.exit(GameLauncher.Status.LOBBY);
			dialog.close();
		}));

		dialog.addAction(new AndroidDialog.Action().setText("Resume").setClickListener(button -> {
			game.settings.pause = false;
			AudioUtil.resume();
			dialog.close();
		})).addSpace(30);

		dialog.show();
	}
	
	@Override
	public void onBackPressed() {
		pause();
	}
	
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