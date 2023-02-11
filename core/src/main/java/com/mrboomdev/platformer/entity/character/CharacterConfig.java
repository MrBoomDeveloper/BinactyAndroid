package com.mrboomdev.platformer.entity.character;

public class CharacterConfig {
	public String id, name, skin;
	public float[] bodySize;
	public Stats stats;
	
	public CharacterConfig build() {
		stats.maxHealth = stats.health;
		return this;
	}
	
	public static class Stats {
		public int health, maxHealth, damage;
		public float speed, stamina, shield;
	}
}