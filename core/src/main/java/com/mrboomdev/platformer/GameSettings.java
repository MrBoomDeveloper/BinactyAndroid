package com.mrboomdev.platformer;

public class GameSettings {
	public String playerName = "Player";
	public int screenInset = 60;
	public int musicVolume = 100, soundsVolume = 100;
	public boolean debugRenderer, debugValues, debugStage;
	public int botsCount = 8;
	
	public GameSettings setPlayerName(String name) {
		this.playerName = name;
		return this;
	}
	
	public GameSettings setScreenInset(int inset) {
		this.screenInset = inset;
		return this;
	}
	
	public GameSettings setVolume(int musicVolume, int soundsVolume) {
		this.musicVolume = musicVolume;
		this.soundsVolume = soundsVolume;
		return this;
	}
	
	public GameSettings setDebug(boolean debugValues, boolean debugRenderer, boolean debugStage) {
		this.debugValues = debugValues;
		this.debugRenderer = debugRenderer;
		this.debugStage = debugStage;
		return this;
	}
}