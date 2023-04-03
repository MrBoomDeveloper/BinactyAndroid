package com.mrboomdev.platformer.entity;

public class Entity {
	public static final short NONE = 0,
							  CHARACTER = 1,
							  BLOCK = 2,
							  BULLET = 4,
							  ATTACK = 8,
							  LIGHT = 16,
							  WEAPON = 32,
							  CHARACTER_BOTTOM = 64,
							  TILE_BOTTOM = 128;
	
	public static final float DASH_COST = 30;
	public static final float DASH_DELAY = 1f;
	public static final float DASH_DURATION = .25f;
	public static final float DASH_SPEED = 5;
	
	public enum Animation {
		IDLE, BORED,
		WALK, RUN, DASH,
		ATTACK, SHOOT,
		DAMAGE, DEATH
	}
	
	public enum Target {
		EVERYONE,
		MAIN_PLAYER
	}
}