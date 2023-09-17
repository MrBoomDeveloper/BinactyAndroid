package com.mrboomdev.platformer.projectile;

import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.character.CharacterEntity;

public abstract class Projectile {

	public int getDamage() {
		return 0;
	}

	public Vector2 getPower() {
		return Vector2.Zero;
	}

	public CharacterEntity getOwner() {
		return null;
	}
}