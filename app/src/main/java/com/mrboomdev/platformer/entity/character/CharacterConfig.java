package com.mrboomdev.platformer.entity.character;

public class CharacterConfig {
	public String id, name, skin;
	public float[] bodySize;
	public float[] body3D;
	public float[] lightOffset = {0, 0};
	public Stats stats;
	
	public CharacterConfig build() {
		stats.maxHealth = stats.health;
		stats.maxStamina = stats.stamina;
		return this;
	}
	
	public static class Stats {
		public int health;
		public int maxHealth;
		public float stamina;
		public float maxStamina;
		public float speed;
		public float shield;
		public int damage;
	}
}