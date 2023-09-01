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
		@Json(name = "attack")
		ATTACK(AnimationPriority.ACTION),
		@Json(name = "shoot_pistol")
		SHOOT_PISTOL(AnimationPriority.ACTION),
		@Json(name = "damage")
		DAMAGE(AnimationPriority.ACTION),
		@Json(name = "death")
		DEATH(AnimationPriority.STATE_IMPORTANT);
		private List<AnimationType> alternatives;
		private final AnimationPriority priority;

		static {
			IDLE.alternatives = List.of(WALK);
			WALK.alternatives = List.of(RUN);
			RUN.alternatives = List.of(WALK, DASH);
			DAMAGE.alternatives = List.of(WALK);
			SHOOT_PISTOL.alternatives = List.of(ATTACK);
			ATTACK.alternatives = List.of(SHOOT_PISTOL);
			ACT.alternatives = List.of(ATTACK, SHOOT_PISTOL);
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

		public boolean isImportant() {
			return getPriority() == AnimationPriority.STATE_IMPORTANT || getPriority() == AnimationPriority.ACTION_IMPORTANT;
		}

		public List<AnimationType> getAlternatives() {
			return alternatives;
		}
	}
	
	public enum Target {
		@Json(name = "everyone")
		EVERYONE,
		@Json(name = "main_player")
		MAIN_PLAYER,
		@Json(name = "near_enemy")
		NEAR_ENEMY,
		PLAYERS,
		BOTS,
		TILES
	}

	public enum Overridable {
		@Json(name = "until_end")
		UNTIL_END,
		@Json(name = "anytime")
		ANYTIME,
		@Json(name = "anytime_other")
		ANYTIME_OTHER,
		@Json(name = "never")
		NEVER
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
		@Json(name = "action_delay")
		public float actionDelay;
		@Json(name = "is_action")
		public boolean isAction;
		public float delay;
		public Overridable overridable;
		public boolean force;
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

	public static class LightDeclaration {
		public LightType type;
		public LightDuration duration;
		public float[] color;
		public float distance, radius;
		public int rays;

		public enum LightDuration {
			@Json(name = "always")
			ALWAYS,
			@Json(name = "during_usage")
			DURING_USAGE
		}

		public enum LightType {
			@Json(name = "directional")
			DIRECTIONAL,
			@Json(name = "point")
			POINT
		}
	}
}