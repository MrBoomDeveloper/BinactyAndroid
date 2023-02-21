package com.mrboomdev.platformer.gameplay.gamemode;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GamemodeManager {
	private static GamamodeManager instance;
	
	public static GamemodeManager getInstance() {
		if(instance == null) instance = new GamemodeManager();
		return instance;
	}
	
	private GamemodeManager() {
		
	}
	
	public void draw(SpriteBatch batch) {
		
	}
}