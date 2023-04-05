package com.mrboomdev.platformer.script.bridge;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.gamemode.GamemodeFunction;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.util.io.FileUtil;

public class GameBridge {
	private GameHolder game = GameHolder.getInstance();
	private GameListener listener;
	
	public void setListener(GameListener listener) {
		this.listener = listener;
	}
	
	public void callListener(Function function) {
		if(listener == null) return;
		switch(function) {
			case START: listener.start(); break;
			case BUILD: listener.build(); break;
			case END: listener.end(); break;
		}
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
	
	public void over(CharacterEntity character, boolean isWin) {
		if(character == game.settings.mainPlayer) {
			game.environment.gamemode.runFunction(new GamemodeFunction(GamemodeFunction.Action.GAME_OVER, null, null));
			return;
		}
	}
	
	public void ready() {
		
	}
	
	public interface GameListener {
		public void start();
		public void build();
		public void end();
	}
	
	public enum Function {
		START,
		BUILD,
		END
	}
}