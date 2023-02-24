package com.mrboomdev.platformer.game;

import com.badlogic.gdx.Gdx;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;

public class GameDebugLauncher extends GameLauncher {
	
	@Override
	public void exit() {
		Gdx.app.postRunnable(() -> {
			var game = GameHolder.getInstance();
			game.setScreen(new LoadingScreen(LoadingScreen.LoadScene.GAMEPLAY));
		});
	}
}