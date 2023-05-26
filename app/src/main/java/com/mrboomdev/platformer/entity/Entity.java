package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mrboomdev.platformer.util.ColorUtil;
import com.squareup.moshi.Json;
import java.util.Map;

public class Entity {

	public static final short NONE = 0,
			CHARACTER = 1,
			CHARACTER_BOTTOM = 2,
			BULLET = 4,
			ATTACK = 8,
			LIGHT = 16,
			BLOCK = 32,
			TILE_BOTTOM = 64,
			INTRACTABLE = 128;
	
	public static final float DASH_COST = 30;
	public static final float DASH_DELAY = 1f;
	public static final float DASH_DURATION = .25f;

	public enum AnimationType {
		@Json(name = "idle")   IDLE,
		@Json(name = "bored")  BORED,
		@Json(name = "walk")   WALK,
		@Json(name = "run")    RUN,
		@Json(name = "dash")   DASH,
		@Json(name = "attack") ATTACK,
		@Json(name = "shoot")  SHOOT,
		@Json(name = "damage") DAMAGE,
		@Json(name = "death")  DEATH
	}
	
	public enum Target {
		@Json(name = "everyone") EVERYONE,
		@Json(name = "main_player") MAIN_PLAYER,
		@Json(name = "near_enemy") NEAR_ENEMY
	}
	
	public static class Frame {
		public String texture = "texture.png";
		public int[] region;
		public float[] size, position;
		@Json(name = "hand_position")
		public float[] handPosition;
		@Json(ignore = true)
		public Sprite sprite;
		
		public void fillEmpty(Animation parent) {
			if(handPosition == null) handPosition = parent.handPosition != null ? parent.handPosition : new float[]{0, 0};
			if(position == null) position = parent.position != null ? parent.position : new float[]{0, 0};
			if(size == null) size = parent.size;
			if(region == null) region = parent.region;
			if(texture == null) texture = parent.texture;
		}
	}
	
	public static class Animation extends Frame {
		public float delay;
		public Frame[] frames;
		public PlayMode mode;
	}

	@SuppressWarnings("unused")
	public static class States {
		public String initial;
		public Map<String, State> types;
	}
	
	public static class State {
		public Frame[] skin;
	}
	
	public static class Light {
		public ColorUtil color;
		public float distance = 5;
		public float[] offset = {0, 0};
		public int rays = 6;
	}
	
	public static class Stats {
		public int health;
		public int maxHealth;
		public float stamina;
		public float maxStamina;
		public float speed;
		public int damage;
	}
}