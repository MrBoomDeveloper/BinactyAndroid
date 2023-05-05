package com.mrboomdev.platformer.script.bridge;

import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.bot.BotBrain;
import com.mrboomdev.platformer.entity.bot.BotBrainBuilder;
import com.mrboomdev.platformer.entity.character.CharacterCreator;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.entity.character.CharacterGroup;
import com.mrboomdev.platformer.entity.item.Item;
import com.mrboomdev.platformer.environment.map.MapEntity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;

public class EntitiesBridge {
	private GameHolder game = GameHolder.getInstance();
	private EntityManager entities = game.environment.entities;
	private EntityListener listener;
	private FileUtil source;
	
	public EntitiesBridge(FileUtil source) {
		this.source = source;
	}
	
	public void callListener(Function function, Object... args) {
		if(listener == null) return;
		switch(function) {
			case DIED: listener.died((CharacterEntity)args[0]); break;
		}
	}
	
	public CharacterCreator createCharacter(String name) {
		if(!entities.presets.containsKey(name)) {
			throw BoomException.builder("Tried to create a character, which wasn't been loaded: ").addQuoted(name).build();
		}
		var character = entities.presets.get(name).cpy("", source.getParent().goTo(name));
		var creator = new CharacterCreator(character);
		return creator;
	}
	
	public CharacterEntity getCharacter(Entity.Target target) {
		switch(target) {
			case MAIN_PLAYER: return game.settings.mainPlayer;
			case EVERYONE: return new CharacterGroup(game.environment.entities.characters);
			default: return null;
		}
	}
	
	public Item createItem(String name) {
		if(!entities.itemPresets.containsKey(name)) {
			throw BoomException.builder("Tried to create a item, which wasn't been loaded: ").addQuoted(name).build();
		}
		var item = entities.itemPresets.get(name).cpy();
		return item;
	}

	public void setListener(EntityListener listener) {
		this.listener = listener;
	}
	
	public BotBrainBuilder createBrain() {
		return new BotBrainBuilder();
	}
	
	public interface EntityListener {
		void died(CharacterEntity character);
	}
	
	public enum Function {
		DIED
	}
}