package com.mrboomdev.platformer.script.bridge;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.bot.BotBrainBuilder;
import com.mrboomdev.platformer.entity.character.CharacterCreator;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.entity.item.Item;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.pack.PackLoader;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;

@SuppressWarnings("unused")
public class EntitiesBridge {
	private final GameHolder game = GameHolder.getInstance();
	private final EntityManager entities = game.environment.entities;
	private final FileUtil source;
	private EntityListener listener;
	
	public EntitiesBridge(FileUtil source) {
		this.source = source;
	}
	
	public void callListener(Function function, Object... args) {
		if(listener == null) return;
		switch(function) {
			case DIED: listener.died((CharacterEntity)args[0]); break;
			case SPAWNED: listener.spawned((CharacterEntity)args[0]); break;
			case DAMAGED: listener.damaged((CharacterEntity)args[0]); break;
			case ATTACKED: listener.attacked((CharacterEntity)args[0]); break;
		}
	}
	
	public CharacterCreator createCharacter(String name) {
		if(!entities.presets.containsKey(name)) {
			throw BoomException.builder("Tried to create a character, which wasn't been loaded: ").addQuoted(name).build();
		}

		var character = entities.presets.get(name).cpy("", PackLoader.resolvePath(source.getParent(), name));
		return new CharacterCreator(character);
	}
	
	public CharacterEntity getCharacter(@NonNull Entity.Target target) {
		switch(target) {
			case MAIN_PLAYER: return game.settings.mainPlayer;
			default: return null;
		}
	}
	
	public Item createItem(String name) {
		if(!entities.itemPresets.containsKey(name)) {
			throw BoomException.builder("Tried to create a item, which wasn't been loaded: ").addQuoted(name).build();
		}
		return entities.itemPresets.get(name).cpy();
	}

	public void setListener(EntityListener listener) {
		this.listener = listener;
	}
	
	public BotBrainBuilder createBrain() {
		return new BotBrainBuilder();
	}
	
	public interface EntityListener {
		void died(CharacterEntity character);
		void damaged(CharacterEntity entity);
		void attacked(CharacterEntity entity);
		void spawned(CharacterEntity entity);
	}
	
	public enum Function {
		DIED,
		DAMAGED,
		ATTACKED,
		SPAWNED
	}
}