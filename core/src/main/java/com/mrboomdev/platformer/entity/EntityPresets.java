package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import java.util.HashMap;

public class EntityPresets {
	private static final String listFile = "world/player/list.json";
	private static final String namesFile = "world/player/bots.json";
	private Array<String> characters = new Array<>();
	private Array<String> names = new Array<>();
	
	public CharacterEntity getRandomCharacter() {
		return new CharacterEntity(getRandomName())
			.setConfigFromJson(Gdx.files.internal(EntityManager.entitiesDirectory + characters.random() + "/manifest.json").readString());
	}
	
	public String getRandomName() {
		if(names.notEmpty()) {
			String result = names.random();
			names.removeValue(result, true);
			return result;
		}
		return "Player" + (int)(Math.random() * 9999);
	}
	
	public EntityPresets merge(EntityPresets presets) {
		characters = presets.characters;
		return this;
	}
	
	public static EntityPresets getInternal() {
		Gson gson = new Gson();
		List list = gson.fromJson(Gdx.files.internal(listFile).readString(), List.class);
		
		EntityPresets presets = new EntityPresets();
		presets.names.addAll(gson.fromJson(Gdx.files.internal(namesFile).readString(), String[].class));
		presets.characters.addAll(list.characters);
		
		return presets;
	}
	
	private class Preset {
		public Source source;
	}
	
	private enum Source {
		INTERNAL,
		STORAGE,
		INTERNET
	}
	
	private class List {
		public String[] characters, weapons;
	}
}