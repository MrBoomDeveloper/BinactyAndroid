package com.mrboomdev.platformer.game;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.online.OnlineManager;
import com.mrboomdev.platformer.util.AudioUtil;

public class GameSettings {
	public String playerName = "Player";
	public CharacterEntity mainPlayer;
	public int screenInset = 60;
	public boolean enableEditor, pause, ignoreScriptErrors, isBeta;
	public boolean debugRenderer, debugValues, debugRaysDisable, debugStage;
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