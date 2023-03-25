package com.mrboomdev.platformer.entity;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.bot.BotBrain;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.map.MapEntity;
import com.mrboomdev.platformer.game.GameHolder;
import java.util.HashMap;

public class EntityManager {
	private World world;
	private RayHandler rayHandler;
	public PointLight mainLight;
	private EntityPresets presets = new EntityPresets();
	private Array<Spawn> spawns = new Array<>();
	public static final String entitiesDirectory = "world/player/characters/";
	public HashMap<String, CharacterEntity> characters = new HashMap<>();
	private GameHolder game = GameHolder.getInstance();
	public static EntityManager instance;
	
	public EntityManager(World world, RayHandler rayHandler) {
		this.world = world;
		this.rayHandler = rayHandler;
		instance = this;
	}
	
	public void addCharacter(CharacterEntity character) {
		characters.put(character.name, character);
		GameHolder.getInstance().environment.map.objects.add(new MapEntity(character));
		if(character == GameHolder.getInstance().settings.mainPlayer) {
			character.body.setTransform(22, -14, 0);
			return;
		}
		character.body.setTransform(getFreeSpawn(), 0);
	}
	
	public void setMain(CharacterEntity character) {
		if(mainLight == null) {
			mainLight = new PointLight(rayHandler, 100, game.environment.map.atmosphere.playerLightColor.getColor(), 10, 0, 0);
			mainLight.setSoftnessLength(1);
			mainLight.setContactFilter(Entity.LIGHT, Entity.NONE, Entity.BLOCK);
		}
		mainLight.attachToBody(character.body, character.config.lightOffset[0], character.config.lightOffset[1]);
	}
	
	public EntityManager addBots(int count) {
		for(int i = 0; i < count; i++) {
			CharacterEntity bot = presets.getRandomCharacter()
				.setBrain(new BotBrain(this))
				.create(world);
			addCharacter(bot);
		}
		return this;
	}
	
	public EntityManager addPresets(EntityPresets presets) {
		this.presets = presets;
		return this;
	}
	
	public EntityManager setSpawnsPositions(Array<Spawn> spawns) {
		this.spawns = spawns;
		return this;
	}
	
	public void render(SpriteBatch batch, OrthographicCamera camera) {
		for(CharacterEntity entity : characters.values()) {
			entity.drawProjectiles(batch);
		}
	}
	
	public CharacterEntity getCharacter(String name) {
		return characters.get(name);
	}
	
	public Array<CharacterEntity> getAllCharacters() {
		Array<CharacterEntity> entities = new Array<>();
		for(CharacterEntity entity : characters.values()) {
			entities.add(entity);
		}
		return entities;
	}
	
	private Spawn getFreeSpawn() {
		Array<Spawn> freeSpawns = new Array<>();
		for(Spawn spawn : this.spawns) {
			if(!spawn.isUsed) freeSpawns.add(spawn);
		}
		
		if(freeSpawns.isEmpty()) {
			return spawns.random();
		} else {
			Spawn freeSpawn = freeSpawns.random();
			freeSpawn.isUsed = true;
			return freeSpawn;
		}
	}
	
	public static class Spawn extends Vector2 {
		public boolean isUsed;
		
		public Spawn(float x, float y) {
			super(x, y);
		}
	}
}