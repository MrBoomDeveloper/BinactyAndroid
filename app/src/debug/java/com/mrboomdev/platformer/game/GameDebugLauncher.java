package com.mrboomdev.platformer.game;

import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.badlogic.gdx.Gdx;
import com.google.android.material.color.DynamicColors;
import com.itsaky.androidide.logsender.LogSender;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;
import com.mrboomdev.platformer.util.AudioUtil;

public class GameDebugLauncher extends GameLauncher {
	private GameDebugMenu menu;
	
	@Override
	public void onCreate(Bundle bundle) {
		LogSender.startLogging(this);
		super.onCreate(bundle);
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
		menu.myView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		menu.myView.setVisibility(View.GONE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			menu.destroy();
		} catch(Exception e) {
			e.printStackTrace();
		}
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
			game.setScreen(new LoadingScreen(LoadingScreen.LoadScene.GAMEPLAY));
		});
	}
}