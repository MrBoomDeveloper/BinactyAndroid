package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Entity {
	public static final short NONE = 0;
	public static final short CHARACTER = 1;
	public static final short BLOCK = 2;
	public static final short BULLET = 4;
	public static final short ATTACK = 8;
	public static final short LIGHT = 16;
	public static final short WEAPON = 32;
	public static final short CHARACTER_BOTTOM = 64;
	
	public static final float DASH_COST = 30;
	public static final float DASH_DELAY = 1f;
	public static final float DASH_DURATION = .25f;
	public static final float DASH_SPEED = 5;
	
	public static final String internalDirectory = "world/player/";
	
	public static FileHandle getInternal(short type, String name, String path) {
		String dir = "";
		if(type == CHARACTER) dir = "characters/";
		if(type == WEAPON) dir = "weapon/";
		return Gdx.files.internal(internalDirectory + dir + name + "/" + path);
	}
	
	public enum Animation {
		IDLE, BORED,
		WALK, RUN, DASH,
		ATTACK, SHOOT,
		DAMAGE, DEATH
	}
}