package com.mrboomdev.platformer.entity;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.entity.item.Item;
import com.mrboomdev.platformer.environment.map.MapEntity;
import com.mrboomdev.platformer.environment.path.PathGraph;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Moshi;

public class EntityManager {
	public ObjectMap<String, CharacterEntity> presets = new ObjectMap<>();
	public ObjectMap<String, Item> itemPresets = new ObjectMap<>();
	public ObjectMap<CharacterEntity, PathGraph> graphs = new ObjectMap<>();
	public Array<CharacterEntity> characters = new Array<>();
	public PointLight mainLight;
	private World world;
	private RayHandler rayHandler;
	private GameHolder game = GameHolder.getInstance();
	
	public EntityManager(World world) {
		this.world = world;
	}
	
	public void setupRayHandler(RayHandler rayHandler) {
		this.rayHandler = rayHandler;
	}
	
	public void loadCharacter(FileUtil file, String id) {
		try {
			Moshi moshi = new Moshi.Builder().build();
			var adapter = moshi.adapter(CharacterEntity.class);
			presets.put(id, adapter.fromJson(file.goTo("manifest.json").readString(true)));
		} catch(Exception e) {
			throw new BoomException(e);
		}
	}
	
	public void loadItem(FileUtil file, String id) {
		try {
			Moshi moshi = new Moshi.Builder().build();
			var adapter = moshi.adapter(Item.class);
			var item = adapter.fromJson(file.goTo("manifest.json").readString(true));
			item.source = file;
			itemPresets.put(id, item);
		} catch(Exception e) {
			throw new BoomException(e);
		}
	}
	
	public void setMain(CharacterEntity character) {
		if(mainLight == null) {
			mainLight = new PointLight(rayHandler, 100, game.environment.map.atmosphere.playerLightColor.getColor(), 10, 0, 0);
			mainLight.setSoftnessLength(1);
			mainLight.setContactFilter(Entity.LIGHT, Entity.NONE, Entity.BLOCK);
		}
		mainLight.attachToBody(character.body, character.config.lightOffset[0], character.config.lightOffset[1]);
	}
	
	public void render(SpriteBatch batch) {
		for(CharacterEntity entity : characters) {
			entity.drawProjectiles(batch);
		}
	}
}