package com.mrboomdev.platformer.script.bridge;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.gamemode.GamemodeFunction;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.util.FunUtil;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;

public class GameBridge {
	private GameHolder game = GameHolder.getInstance();
	private GameListener listener;
	private FileUtil source;
	
	public GameBridge(FileUtil source) {
		this.source = source;
	}
	
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
	
	public void setTimer(Runnable runnable, float delay) {
		FunUtil.setTimer(runnable, delay);
	}
	
	public void load(String type, String path) {
		switch(type) {
			case "music":
				source.getParent().goTo(path).loadAsync(Music.class);
				break;
				
			case "sound":
				source.getParent().goTo(path).loadAsync(Sound.class);
				break;
			
			case "character":
				game.environment.entities.loadCharacter(source.getParent().goTo(path), path);
				break;
			
			case "item":
				game.environment.entities.loadItem(source.getParent().goTo(path), path);
				break;
			
			default: throw BoomException.builder("Failed to load a resource. Unknown type! Type: ").addQuoted(type).append(", Path: ").addQuoted(path).build();
		}
	}
	
	public void over(CharacterEntity character, boolean isWin) {
		if(character == game.settings.mainPlayer) {
			game.environment.gamemode.runFunction(new GamemodeFunction(GamemodeFunction.Action.GAME_OVER, null, null));
			return;
		}
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