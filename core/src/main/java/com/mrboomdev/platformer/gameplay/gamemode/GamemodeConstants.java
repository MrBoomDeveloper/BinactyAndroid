package com.mrboomdev.platformer.gameplay.gamemode;

public class GamemodeConstants {
	
	public enum ActionType {
		TITLE,
		TELEPORT,
		GAME_OVER,
		TIMER_START,
		TIMER_END,
		TIMER_HOUR_CHANGED
	}
	
	public enum Target {
		EVERYONE,
		ALL_PLAYERS,
		ALL_BOTS,
		PLAYER_SPAWN
	}
	
	public enum Direction {
		FORWARD,
		BACKWARD,
		NONE
	}
}