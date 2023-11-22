package com.mrboomdev.platformer.entity.character;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.game.GameHolder;

import org.jetbrains.annotations.Contract;

public class CharacterCreator {
	public CharacterEntity entity;
	private final GameHolder game = GameHolder.getInstance();
	
	public CharacterCreator(CharacterEntity entity) {
		this.entity = entity;
	}

	@NonNull
	@Contract("_, _, _ -> param1")
	public static CharacterEntity create(@NonNull CharacterEntity entity, String id, @NonNull MapManager map) {
		var game = GameHolder.getInstance();

		entity.create(game.environment.world);
		map.addCharacter(id, entity);

		return entity;
	}
	
	public CharacterEntity create(String id, @NonNull MapManager map) {
		return create(entity, id, map);
	}
}