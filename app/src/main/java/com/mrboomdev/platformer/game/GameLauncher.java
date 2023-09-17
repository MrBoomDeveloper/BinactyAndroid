package com.mrboomdev.platformer.game;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AsynchronousAndroidAudio;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mrboomdev.binacty.Constants;
import com.mrboomdev.binacty.game.overlay.OverlayGameover;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.R;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;

import java.io.IOException;
import java.util.Objects;

public class GameLauncher extends AndroidApplication {
	private GameHolder game;
	private boolean isFinished;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.gameplay_parent);

		FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
		FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
		
		var prefs = getSharedPreferences("Save", 0);
		if(!prefs.getBoolean("crashlytics", true) || BuildConfig.DEBUG) {
			crashlytics.setCrashlyticsCollectionEnabled(false);
		}
		
		var settings = new GameSettings(prefs);
		settings.enableEditor = getIntent().getBooleanExtra("enableEditor", false);
		settings.ignoreScriptErrors = BuildConfig.DEBUG;
		game = GameHolder.setInstance(this, settings, new GameAnalytics(analytics));

		try {
			resolveGameFiles();
		} catch(Exception e) {
			e.printStackTrace();

			if(!BuildConfig.DEBUG) exit(Status.CRASH);
		}

		var config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		config.useAccelerometer = false;
		config.useCompass = false;

		var gdxView = initializeForView(game, config);
		LinearLayout gdxParent = findViewById(R.id.gameplay);
		gdxParent.addView(gdxView);

		var overlay = findViewById(R.id.overlay);
		overlay.setVisibility(View.GONE);

		ActivityManager.hideSystemUi(this);
	}

	public void gameOver() {
		runOnUiThread(() -> {
			LinearLayout overlay = findViewById(R.id.overlay);
			overlay.setVisibility(View.VISIBLE);

			var gameoverOverlay = new OverlayGameover(this);
			overlay.addView(gameoverOverlay);

			gameoverOverlay.startAnimation();
		});
	}

	private void resolveGameFiles() throws IOException {
		var level = getIntent().getBundleExtra("level");

		if(level != null) {
			game.envVars.putString("levelId", level.getString("id"));
			game.envVars.putString("levelName", level.getString("name"));
		}

		var engine = Objects.requireNonNullElse(getIntent().getStringExtra("engine"), "BeanShell");
		game.settings.engine = GameSettings.Engine.valueOf(engine.toUpperCase());

		var levelFile = getIntent().getCharSequenceExtra("gamemodeFile");
		var mapFile = getIntent().getCharSequenceExtra("mapFile");
		if(levelFile == null || mapFile == null) return;

		var adapter = Constants.moshi.adapter(FileUtil.class);
		game.gamemodeFile = adapter.fromJson(levelFile.toString());
		game.mapFile = adapter.fromJson(mapFile.toString());
	}
	
	@Override
	public AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config) {
		return new AsynchronousAndroidAudio(context, config);
	}
	
	public void exit(Status status) {
		if(isFinished) return;
		AudioUtil.clear();

		LinearLayout overlay = findViewById(R.id.overlay);
		overlay.removeAllViews();

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
		ActivityManager.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		ActivityManager.onPause();
	}
	
	public enum Status {
		CRASH,
		GAME_OVER,
		LOBBY
	}
}