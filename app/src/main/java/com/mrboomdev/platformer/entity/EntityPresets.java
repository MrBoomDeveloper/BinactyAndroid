package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.util.FileUtil;

public class EntityPresets {
	private static final String listFile = "world/player/list.json";
	private static final String namesFile = "world/player/bots.json";
	private Array<String> characters = new Array<>();
	private Array<String> names = new Array<>();
	
	public CharacterEntity getRandomCharacter() {
		return new CharacterEntity(names.random())
			.setConfig(new FileUtil(
				EntityManager.entitiesDirectory, FileUtil.Source.INTERNAL)
				.goTo(characters.random())
		);
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