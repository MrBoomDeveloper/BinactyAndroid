package com.mrboomdev.platformer.environment.gamemode;

public class GamemodeFunction {
	public Action action;
	public Options options;
	public Conditions conditions;
	public boolean isLong = false;
	public float delay, duration, speed = 1;
	
	public GamemodeFunction(Action action, Options options, Conditions conditions) {
		this.action = action;
		this.options = options;
		this.conditions = conditions;
	}
	
	public static class Options {
		public float time = 300;
		public int direction;
		public float from, to;
		public Target target, toTarget;
		public Mode mode;
		public String text = "Hello, World!";
		public String[] queue;
	}
	
	public class Conditions {
		public Target target;
	}
	
	public enum Action {
		TIMER_SETUP, TIMER_END,
		TELEPORT,
		TITLE, FADE,
		GAME_OVER, DEATH,
		PLAY_MUSIC, STOP_MUSIC
	}
	
	public enum Mode {
		QUEUE_LOOP
	}
	
	public enum Target {
		EVERYONE,
		ALL_PLAYERS,
		ALL_BOTS,
		
		MAIN_PLAYER,
		ANY_PLAYER,
		ANY_BOT,
		
		PLAYER_SPAWN,
		BOT_SPAWN
	}
}