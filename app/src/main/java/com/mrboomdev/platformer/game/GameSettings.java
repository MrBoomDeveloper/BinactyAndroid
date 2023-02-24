package com.mrboomdev.platformer.game;

import android.content.SharedPreferences;

public class GameSettings {
	public String playerName = "Player";
	public int screenInset = 60;
	public int musicVolume = 100, soundsVolume = 100;
	public boolean debugRenderer, debugValues, debugStage;
	public boolean enableEditor = true;
	
	public static GameSettings getFromSharedPreferences(SharedPreferences prefs) {
		var settings = new GameSettings();
		settings.playerName = prefs.getString("nick", "Player");
		
		settings.screenInset = prefs.getInt("inset", 60);
		settings.musicVolume = prefs.getInt("musicVolume", 100);
		settings.soundsVolume = prefs.getInt("soundsVolume", 100);
		
		settings.debugValues = prefs.getBoolean("debug", false);
		settings.debugRenderer = prefs.getBoolean("debugRenderer", false);
		settings.debugStage = prefs.getBoolean("debugStage", false);
		return settings;
	}
}