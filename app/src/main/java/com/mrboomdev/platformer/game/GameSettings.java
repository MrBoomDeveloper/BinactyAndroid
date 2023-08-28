package com.mrboomdev.platformer.game;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.ConstantsKt;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.online.OnlineManager;
import com.mrboomdev.platformer.util.AudioUtil;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;

import java.io.IOException;

public class GameSettings {
	public String playerName = "Player";
	public FileUtil playerCharacter;
	public CharacterEntity mainPlayer;
	public int screenInset = 60;
	public float objectResortDelay = .5f, objectPositionRecacheDelay = 1;
	public boolean enableEditor, pause, ignoreScriptErrors, isBeta;
	public boolean isControlsEnabled, isUiVisible;
	public boolean debugRenderer, debugValues, debugRaysDisable, debugStage, debugCamera;
	public Engine engine;
	
	@NonNull
	public static GameSettings getFromSharedPreferences(@NonNull SharedPreferences prefs) {
		var settings = new GameSettings();
		var online = OnlineManager.getInstance();

		settings.playerName = online.isGuest ? " " : prefs.getString("nick", "Player");
		settings.screenInset = prefs.getInt("inset", 60);
		
		settings.debugValues = prefs.getBoolean("debug", false);
		settings.debugRenderer = prefs.getBoolean("debugRenderer", false);
		settings.debugRaysDisable = prefs.getBoolean("debugRaysDisable", false);
		settings.isBeta = prefs.getBoolean("beta", false);

		settings.objectPositionRecacheDelay = prefs.getFloat("objectPositionRecacheDelay", 1);
		settings.objectResortDelay = prefs.getFloat("objectResortDelay", .5f);

		var fileAdapter = ConstantsKt.getMoshi().adapter(FileUtil.class);
		var defaultCharacterFile = "{\"source\":\"INTERNAL\",\"path\":\"packs/official/src/characters/klarrie\"}";

		try {
			var characterFile = prefs.getString("playerCharacter", defaultCharacterFile);
			settings.playerCharacter = fileAdapter.fromJson(characterFile);
		} catch(IOException e) {
			e.printStackTrace();

			try {
				settings.playerCharacter = fileAdapter.fromJson(defaultCharacterFile);
			} catch(IOException ex) {
				throw new BoomException("Failed to load character file", ex);
			}
		}
		
		if(prefs.getBoolean("forceEditor", false)) settings.enableEditor = true;

		AudioUtil.setVolume(prefs.getInt("musicVolume", 100) / 100f, prefs.getInt("soundsVolume", 100) / 100f);
		return settings;
	}

	public enum Engine {
		BEANSHELL,
		JVM,
		JS,
		NATIVE
	}
}