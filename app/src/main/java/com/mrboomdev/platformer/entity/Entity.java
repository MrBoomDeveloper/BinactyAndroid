package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mrboomdev.platformer.util.ColorUtil;
import com.squareup.moshi.Json;

import java.util.List;
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

	public enum AnimationPriority {
		STATE,
		ACTION,
		STATE_IMPORTANT,
		ACTION_IMPORTANT,
	}

	public enum AnimationType {
		CURRENT(AnimationPriority.STATE),
		@Json(name = "idle")
		IDLE(AnimationPriority.STATE),
		@Json(name = "bored")
		BORED(AnimationPriority.STATE),
		@Json(name = "walk")
		WALK(AnimationPriority.STATE),
		@Json(name = "run")
		RUN(AnimationPriority.STATE),
		@Json(name = "dash")
		DASH(AnimationPriority.ACTION),
		@Json(name = "act")
		ACT(AnimationPriority.ACTION),
		@Json(name = "aim_pistol")
		AIM_PISTOL(AnimationPriority.STATE_IMPORTANT),
		@Json(name = "aim_pistol_walk")
		AIM_PISTOL_WALK(AnimationPriority.STATE_IMPORTANT),
		@Json(name = "attack")
		ATTACK(AnimationPriority.ACTION),
		@Json(name = "shoot")
		SHOOT(AnimationPriority.ACTION),
		@Json(name = "damage")
		DAMAGE(AnimationPriority.ACTION),
		@Json(name = "death")
		DEATH(AnimationPriority.STATE_IMPORTANT);

		private List<AnimationType> alternatives;
		private final AnimationPriority priority;

		static {
			IDLE.alternatives = List.of(WALK);
			WALK.alternatives = List.of(RUN, IDLE);
			RUN.alternatives = List.of(WALK, DASH, IDLE);
			DAMAGE.alternatives = List.of(WALK);
			DASH.alternatives = List.of(IDLE);
			AIM_PISTOL.alternatives = List.of(IDLE);
			AIM_PISTOL_WALK.alternatives = List.of(AIM_PISTOL);
			DEATH.alternatives = List.of(IDLE);
			SHOOT.alternatives = List.of(ATTACK, IDLE);
			ATTACK.alternatives = List.of(SHOOT, IDLE);
			ACT.alternatives = List.of(ATTACK, SHOOT, IDLE);
		}

		AnimationType(AnimationPriority priority) {
			this.priority = priority;
		}

		public AnimationPriority getPriority() {
			return this.priority;
		}

		public boolean isAction() {
			return getPriority() == AnimationPriority.ACTION || getPriority() == AnimationPriority.ACTION_IMPORTANT;
		}

		public List<AnimationType> getAlternatives() {
			return alternatives;
		}
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