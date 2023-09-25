package com.mrboomdev.platformer.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.color.DynamicColors;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;

public class GameDebugLauncher extends GameLauncher {
	private static GameSettings previousSettings;
	private GameDebugMenu menu;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		var game = GameHolder.getInstance();
		FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false);

		game.gamemodeFile = FileUtil.internal("packs/fnaf/gamemode.java");
		game.mapFile = FileUtil.internal("packs/fnaf/maps/fnafMap1.json");
		AudioUtil.setVolume(1, 1);
		
		menu = new GameDebugMenu(this);
		DynamicColors.applyToActivityIfAvailable(this);

		restorePreviousSettings();
		previousSettings = game.settings;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		menu.onResume();

		if(menu.myView != null) {
			menu.myView.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();

		if(menu.myView != null) {
			menu.myView.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		finishAffinity();
	}
	
	@Override
	public void onBackPressed() {
		menu.destroy();
	}
	
	@Override
	public void exit(ExitStatus status) {
		AudioUtil.clear();

		if(status == ExitStatus.CRASH || status == ExitStatus.LOBBY) {
			finishAffinity();
			return;
		}

		runOnUiThread(() -> {
			var intent = new Intent(this, GameDebugLauncher.class);
			startActivity(intent);
			finish();
		});
	}

	private void restorePreviousSettings() {
		if(previousSettings == null) return;
		var game = GameHolder.getInstance();

		game.settings.isControlsEnabled = previousSettings.isControlsEnabled;
		game.settings.isUiVisible = previousSettings.isUiVisible;

		game.settings.debugCamera = previousSettings.debugCamera;
		game.settings.debugRaysDisable = previousSettings.debugRaysDisable;
		game.settings.debugRenderer = previousSettings.debugRenderer;
		game.settings.debugValues = previousSettings.debugValues;
		game.settings.debugStage = previousSettings.debugStage;
	}
}