package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mrboomdev.platformer.util.ColorUtil;
import com.squareup.moshi.Json;
import java.util.Map;

public class Entity {

	public static final short NONE = 0,
		CHARACTER = 1,
		BLOCK = 2,
		BULLET = 4,
		ATTACK = 8,
		LIGHT = 16,
		WEAPON = 32,
		CHARACTER_BOTTOM = 64,
		TILE_BOTTOM = 128,
		INTERACTABLE = 256;
	
	public static final float DASH_COST = 30,
		DASH_DELAY = 1f,
		DASH_DURATION = .25f,
		DASH_SPEED = 5;
	
	public enum Animation {
		IDLE, BORED,
		WALK, RUN, DASH,
		ATTACK, SHOOT,
		DAMAGE, DEATH
	}
	
	public enum Target {
		EVERYONE,
		MAIN_PLAYER,
		CONNECTED
	}
	
	public static class Frame {
		public String texture = "texture.png";
		public float[] size, position;
		public int[] region;
		@Json(name = "hand_position") public float[] handPosition;
	}
	
	public static class States {
		public String initial;
		public Map<String, State> types;
	}
	
	public static class State {
		public Frame[] skin;
		public Light[] light;
	}
	
	public static class Light {
		public ColorUtil color;
		public float distance = 5;
		public float[] offset = {0, 0};
		public int rays = 6;
	}
}