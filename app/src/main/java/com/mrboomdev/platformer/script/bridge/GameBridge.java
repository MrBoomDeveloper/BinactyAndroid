package com.mrboomdev.platformer.script.bridge;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.io.FileUtil;

public class GameBridge {
	private GameHolder game = GameHolder.getInstance();
	
	public void setListener() {
		
	}
	
	public void load(String type, String path) {
		switch(type) {
			case "music": {
				game.assets.load("packs/fnaf/" + path, Music.class);
				break;
			}
			case "sound": {
				game.assets.load("packs/fnaf/" + path, Sound.class);
				break;
			}
			case "character": {
				game.environment.entities.loadCharacter(new FileUtil("packs/fnaf/" + path, FileUtil.Source.INTERNAL), path);
				break;
			}
		}
	}
	
	public void over(Entity.Target target, boolean isWin) {
		
	}
	
	public void ready() {
		
	}
}