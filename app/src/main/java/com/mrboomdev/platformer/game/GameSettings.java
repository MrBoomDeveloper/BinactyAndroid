package com.mrboomdev.platformer.game;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.mrboomdev.binacty.Constants;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.online.OnlineManager;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;

import java.io.IOException;

public class GameSettings {
	public String playerName = "Player";
	public FileUtil playerCharacter;
	public CharacterEntity mainPlayer;
	public int screenInset = 60;
	public float objectPositionRecacheDelay = 1;
	public boolean enableEditor, pause, ignoreScriptErrors, isBeta;
	public boolean isControlsEnabled, isUiVisible;
	public boolean debugRenderer, debugValues, debugRaysDisable, debugStage, debugCamera;
	public Engine engine;

	public GameSettings() {}

	public GameSettings(@NonNull SharedPreferences prefs) {
		var online = OnlineManager.getInstance();

		this.playerName = online.isGuest ? " " : prefs.getString("nick", "Player");
		this.screenInset = prefs.getInt("inset", 60);
		
		this.debugValues = prefs.getBoolean("debug", false);
		this.debugRenderer = prefs.getBoolean("debugRenderer", false);
		this.debugRaysDisable = prefs.getBoolean("debugRaysDisable", false);
		this.isBeta = prefs.getBoolean("beta", false);

		this.objectPositionRecacheDelay = prefs.getFloat("objectPositionRecacheDelay", 1);

		var fileAdapter = Constants.moshi.adapter(FileUtil.class);
		var defaultCharacterFile = "{\"source\":\"INTERNAL\",\"path\":\"packs/official/src/characters/klarrie\"}";

		try {
			var characterFile = prefs.getString("playerCharacter", defaultCharacterFile);
			this.playerCharacter = fileAdapter.fromJson(characterFile);
		} catch(IOException e) {
			e.printStackTrace();

			try {
				this.playerCharacter = fileAdapter.fromJson(defaultCharacterFile);
			} catch(IOException ex) {
				throw new BoomException("Failed to load character file", ex);
			}
		}
		
		if(prefs.getBoolean("forceEditor", false)) {
			this.enableEditor = true;
		}

		AudioUtil.setVolume(
				prefs.getInt("musicVolume", 100) / 100f,
				prefs.getInt("soundsVolume", 100) / 100f);
	}

	public enum Engine {
		BEANSHELL,
		JVM,
		JS,
		NATIVE
	}
}