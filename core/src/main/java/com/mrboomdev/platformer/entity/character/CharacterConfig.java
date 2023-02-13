package com.mrboomdev.platformer.entity.character;

public class CharacterConfig {
	public String id, name, skin;
	public float[] bodySize;
	public Stats stats;
	
	public CharacterConfig build() {
		stats.maxHealth = stats.health;
		stats.maxStamina = stats.stamina;
		return this;
	}
	
	public static class Stats {
		public int health, maxHealth;
		public int stamina, maxStamina;
		public float speed, shield;
		public int damage;
	}
}