package com.mrboomdev.platformer.environment.gamemode;
import com.mrboomdev.platformer.util.Direction;

public class GamemodeFunction {
	public Action action;
	public Options options;
	public Conditions conditions;
	public boolean isLong = false;
	public float delay = 0;
	
	public class Options {
		public int[] timerStart = {0, 0};
		public int[] timerEnd = {10, 0};
		public int direction;
		public float duration;
		public float from, to;
		public Target[] target;
		public Target toSpecial;
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
		ALL_BOTS
	}
}