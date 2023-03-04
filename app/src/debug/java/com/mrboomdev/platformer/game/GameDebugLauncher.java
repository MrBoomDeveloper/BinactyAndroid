package com.mrboomdev.platformer.game;

import android.os.Bundle;
import com.badlogic.gdx.Gdx;
import com.google.android.material.color.DynamicColors;
import com.itsaky.androidide.logsender.LogSender;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;

public class GameDebugLauncher extends GameLauncher {
	private GameDebugMenu menu;
	
	@Override
	public void onCreate(Bundle bundle) {
		LogSender.startLogging(this);
		super.onCreate(bundle);
		menu = new GameDebugMenu(this);
		DynamicColors.applyToActivityIfAvailable(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		menu.onResume();
	}
	
	@Override
	public void onBackPressed() {
		menu.destroy();
	}
	
	@Override
	public void exit() {
		Gdx.app.postRunnable(() -> {
			var game = GameHolder.getInstance();
			game.setScreen(new LoadingScreen(LoadingScreen.LoadScene.GAMEPLAY));
		});
	}
}