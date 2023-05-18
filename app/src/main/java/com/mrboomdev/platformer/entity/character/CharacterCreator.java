package com.mrboomdev.platformer.entity.character;

import androidx.annotation.NonNull;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.bot.BotBrain;
import com.mrboomdev.platformer.environment.map.MapEntity;
import com.mrboomdev.platformer.game.GameHolder;

public class CharacterCreator {
	public CharacterEntity entity;
	private final GameHolder game = GameHolder.getInstance();
	private String spawnTile;
	
	public CharacterCreator(CharacterEntity entity) {
		this.entity = entity;
	}
	
	public CharacterCreator setSpawnTiles(String[] tiles) {
		spawnTile = new Array<>(tiles).random();
		return this;
	}

	public CharacterCreator setBot(@NonNull BotBrain brain) {
		entity.setBrain(brain.start(game.environment.entities));
		return this;
	}
	
	public CharacterEntity create() {
		entity.create(game.environment.world);
		if(spawnTile != null && spawnTile.startsWith("#id:")) {
			var results = game.environment.map.tilesMap.values().toArray()
					.select((var tile) -> tile.id != null && tile.id.equals(spawnTile.substring(4)));

			for(var result : results) {
				entity.body.setTransform(result.getPosition(false), 0);
			}
		} else {
			entity.body.setTransform(22, -14, 0);
		}
		game.environment.map.objects.add(new MapEntity(entity));
		game.environment.entities.characters.add(entity);
		return entity;
	}
}