package com.mrboomdev.platformer.environment.gamemode;

import com.mrboomdev.platformer.util.Direction;

public class GamemodeFunction {
	public Action action;
	public Options options;
	public Conditions conditions;
	public boolean isLong = false;
	public float delay, duration, speed = 1;
	
	public class Options {
		public float time = 300;
		public int direction;
		public float from, to;
		public Target[] target;
		public Target toTarget;
		public Mode mode;
		public String text = "Hello, World!";
		public String[] queue;
	}
	
	public class Conditions {
		public int timerHour = 999;
		public Target[] target;
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
		
		PLAYER_SPAWN,
		BOT_SPAWN
	}
}