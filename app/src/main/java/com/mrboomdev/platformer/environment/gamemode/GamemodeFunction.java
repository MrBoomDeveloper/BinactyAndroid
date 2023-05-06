package com.mrboomdev.platformer.environment.gamemode;

public class GamemodeFunction {
	public Action action;
	public Options options;
	public boolean isLong = false;
	public float duration, speed = 1;
	
	public GamemodeFunction(Action action, Options options) {
		this.action = action;
		this.options = options;
	}
	
	public static class Options {
		public float time = 300;
		public int direction;
		public float from, to;
		public String text = "Hello, World!";
	}
	
	public enum Action {
		TIMER_SETUP,
		TITLE, FADE,
		GAME_OVER
	}
}