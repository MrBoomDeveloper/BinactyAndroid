package com.mrboomdev.platformer.entity.character;

public abstract class CharacterBrain {
	private CharacterEntity entity;
	
	public void setEntity(CharacterEntity entity) {
		this.entity = entity;
	}

	public CharacterEntity getEntity() {
		return entity;
	}

	public void update() {}

	public void start() {}

	public void end() {}
}