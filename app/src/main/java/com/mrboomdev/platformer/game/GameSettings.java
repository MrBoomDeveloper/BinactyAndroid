package com.mrboomdev.platformer.game;

import android.content.SharedPreferences;
import com.mrboomdev.platformer.entity.character.CharacterEntity;

public class GameSettings {
	public String playerName = "Player";
	public CharacterEntity mainPlayer;
	
	public int screenInset = 60;
	public int musicVolume = 100, soundsVolume = 100;
	
	public boolean enableEditor = true;
	public boolean debugRenderer, debugValues, debugStage, debugRaysDisable;
	
	public static GameSettings getFromSharedPreferences(SharedPreferences prefs) {
		var settings = new GameSettings();
		settings.playerName = prefs.getString("nick", "Player");
		
		settings.screenInset = prefs.getInt("inset", 60);
		settings.musicVolume = prefs.getInt("musicVolume", 100);
		settings.soundsVolume = prefs.getInt("soundsVolume", 100);
		
		settings.debugValues = prefs.getBoolean("debug", false);
		settings.debugRenderer = prefs.getBoolean("debugRenderer", false);
		settings.debugStage = prefs.getBoolean("debugStage", false);
		settings.debugRaysDisable = prefs.getBoolean("debugRaysDisable", false);
		return settings;
	}
}