package com.mrboomdev.platformer.script.bridge;

import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.bot.BotBrain;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.map.MapEntity;
import com.mrboomdev.platformer.game.GameHolder;

public class EntitiesBridge {
	private GameHolder game = GameHolder.getInstance();
	private EntityManager entities = game.environment.entities;
	public EntityListener listener;
	
	public void createCharacter(String name) {
		if(!entities.presets.containsKey(name)) {
			game.analytics.error("Script", "Tried to create the character, which wasn't been loaded: " + name);
			return;
		}
		var character = entities.presets.get(name).cpy("");
		character.create(game.environment.world);
		character.setBrain(new BotBrain(entities));
		character.body.setTransform(36, 20, 0);
		game.environment.map.objects.add(new MapEntity(character));
		entities.characters.add(character);
	}

	public void setListener(EntityListener listener) {
		this.listener = listener;
	}
	
	public interface EntityListener {
		void died(CharacterEntity character);
	}
}