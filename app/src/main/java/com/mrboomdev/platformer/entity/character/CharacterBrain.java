package com.mrboomdev.platformer.entity.character;

public abstract class CharacterBrain {
	public CharacterEntity entity;
	
	public CharacterBrain setEntity(CharacterEntity entity) {
		this.entity = entity;
		return this;
	}

	public void update() {}
}