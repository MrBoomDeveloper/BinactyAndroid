package com.mrboomdev.platformer.entity.character;

import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.bot.BotBrain;
import com.mrboomdev.platformer.environment.map.MapEntity;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;

public class CharacterCreator {
	private GameHolder game = GameHolder.getInstance();
	private CharacterEntity entity;
	private String spawnTile;
	
	public CharacterCreator(CharacterEntity entity) {
		this.entity = entity;
	}
	
	public CharacterCreator setSpawnTiles(String[] tiles) {
		spawnTile = new Array<String>(tiles).random();
		return this;
	}

	public CharacterCreator setBot() {
		entity.setBrain(new BotBrain(game.environment.entities));
		return this;
	}
	
	public void create() {
		entity.create(game.environment.world);
		if(spawnTile.startsWith("#id:")) {
			var results = game.environment.map.tilesMap.values().toArray().select((var tile) -> {
				return tile.id != null && tile.id.equals(spawnTile.substring(4, spawnTile.length()));
			});
			for(var result : results) {
				System.out.println(result.id + " teleported!");
				entity.body.setTransform(result.getPosition(false), 0);
			}
		} else {
			entity.body.setTransform(36, 20, 0);
		}
		game.environment.map.objects.add(new MapEntity(entity));
		game.environment.entities.characters.add(entity);
	}
}