package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.binacty.Constants;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.entity.item.Item;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;

import java.util.Objects;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class EntityManager {
	public ObjectMap<String, CharacterEntity> presets = new ObjectMap<>();
	public ObjectMap<String, Item> itemPresets = new ObjectMap<>();
	public Array<CharacterEntity> characters = new Array<>();
	public PointLight mainLight;
	private RayHandler rayHandler;
	private final GameHolder game = GameHolder.getInstance();
	
	public void setupRayHandler(RayHandler rayHandler) {
		this.rayHandler = rayHandler;
	}
	
	public void loadCharacter(FileUtil file, String id) {
		try {
			var adapter = Constants.moshi.adapter(CharacterEntity.class);

			presets.put(id, adapter.fromJson(file.goTo("manifest.json").readString(true)));
		} catch(Exception e) {
			throw new BoomException("Invalid character config file!", e);
		}
	}
	
	public void loadItem(FileUtil file, String id) {
		try {
			var adapter = Constants.moshi.adapter(Item.class);
			var item = adapter.fromJson(file.goTo("manifest.json").readString(true));

			Objects.requireNonNull(item).source = file;
			itemPresets.put(id, item);
		} catch(Exception e) {
			throw new BoomException("Invalid item config file!", e);
		}
	}
	
	public void setMain(CharacterEntity character) {
		if(mainLight == null) {
			mainLight = new PointLight(rayHandler, 100, game.environment.map.atmosphere.playerLightColor.getColor(), 8, 0, 0);
			mainLight.setSoftnessLength(1);
			mainLight.setContactFilter(Entity.LIGHT, Entity.NONE, Entity.BLOCK);
		}

		mainLight.attachToBody(character.body, character.worldBody.lightOffset[0], character.worldBody.lightOffset[1]);
	}
	
	public void render(SpriteBatch batch) {
		for(CharacterEntity entity : characters) {
			entity.drawProjectiles(batch);
			entity.aimSprite.draw(batch);
		}
	}
}