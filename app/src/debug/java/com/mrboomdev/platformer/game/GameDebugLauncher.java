package com.mrboomdev.platformer.game;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.badlogic.gdx.Gdx;
import com.google.android.material.color.DynamicColors;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;
import com.mrboomdev.platformer.util.AudioUtil;
import com.mrboomdev.platformer.util.io.FileUtil;

public class GameDebugLauncher extends GameLauncher {
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
		
		WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
		var windowController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
		windowController.hide(WindowInsetsCompat.Type.systemBars());
		windowController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
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
	public void exit(Status status) {
		if(status == Status.CRASH || status == Status.LOBBY) {
			finishAffinity();
			return;
		}

		AudioUtil.clear();
		Gdx.app.postRunnable(() -> {
			var game = GameHolder.getInstance();
			game.reset();
			game.setScreen(new LoadingScreen());
		});
	}
}