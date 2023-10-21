package com.mrboomdev.platformer.entity.character;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.game.GameHolder;

public class CharacterCreator {
	public CharacterEntity entity;
	private final GameHolder game = GameHolder.getInstance();
	
	public CharacterCreator(CharacterEntity entity) {
		this.entity = entity;
	}

	public CharacterCreator setBot(@NonNull CharacterBrain brain) {
		entity.setBrain(brain);
		brain.start();

		return this;
	}
	
	public CharacterEntity create(String id, @NonNull MapManager map) {
		entity.create(game.environment.world);
		map.addCharacter(id, entity);

		return entity;
	}
}