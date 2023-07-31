package com.mrboomdev.platformer.script.bridge;

import androidx.annotation.NonNull;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.gamemode.GamemodeFunction;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.pack.PackLoader;
import com.mrboomdev.platformer.util.FunUtil;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;

@SuppressWarnings("unused")
public class GameBridge {
	private final GameHolder game = GameHolder.getInstance();
	private final FileUtil source;
	private GameListener listener;
	
	public GameBridge(FileUtil source) {
		this.source = source;
	}
	
	public void setListener(GameListener listener) {
		this.listener = listener;
	}
	
	public void callListener(Function function) {
		if(listener == null) return;
		try {
			switch(function) {
				case START:
					listener.start();
					break;

				case BUILD:
					listener.build();
					break;

				case END:
					listener.end();
					break;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setPlayerPosition(float x, float y) {
		game.settings.mainPlayer.body.setTransform(x, y, 0);
	}
	
	public void setTimer(Runnable runnable, float delay) {
		FunUtil.setTimer(runnable, delay);
	}
	
	public void load(@NonNull String type, String path) {
		switch(type) {
			case "music":
				PackLoader.resolvePath(source.getParent(), path).loadAsync(Music.class);
				break;
				
			case "sound":
				PackLoader.resolvePath(source.getParent(), path).loadAsync(Sound.class);
				break;
			
			case "character":
				game.environment.entities.loadCharacter(PackLoader.resolvePath(source.getParent(), path), path);
				break;
			
			case "item":
				game.environment.entities.loadItem(PackLoader.resolvePath(source.getParent(), path), path);
				break;
			
			default: throw BoomException.builder("Failed to load a resource. Unknown type! Type: ").addQuoted(type).append(", Path: ").addQuoted(path).build();
		}
	}
	
	public void over(CharacterEntity character, boolean isWin) {
		if(character == game.settings.mainPlayer) {
			game.environment.gamemode.runFunction(new GamemodeFunction(GamemodeFunction.Action.GAME_OVER, null));
		}
	}

	public String getEnvString(String name, String defaultValue) {
		return game.envVars.getString(name, defaultValue);
	}
	
	public interface GameListener {
		void start();
		void build();
		void end();
	}
	
	public enum Function {
		START,
		BUILD,
		END
	}
}