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
		public String text = "Hello, World!";
	}
	
	public class Conditions {
		public int timerHour = 999;
		public Target[] target;
	}
	
	public enum Action {
		TIMER_SETUP, TIMER_END,
		TELEPORT,
		TITLE, FADE,
		GAME_OVER, DEATH
	}
	
	public enum Target {
		EVERYONE,
		ALL_PLAYERS,
		PLAYER_SPAWN,
		BOT_SPAWN,
		ALL_BOTS,
		MAIN_PLAYER
	}
}