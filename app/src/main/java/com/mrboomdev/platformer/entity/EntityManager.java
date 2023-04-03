package com.mrboomdev.platformer.entity;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.platformer.entity.bot.BotBrain;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.map.MapEntity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Moshi;

public class EntityManager {
	public static EntityManager instance;
	public static final String entitiesDirectory = "world/player/characters/";
	public ObjectMap<String, CharacterEntity> presets = new ObjectMap<>();
	public Array<CharacterEntity> characters = new Array<>();
	public PointLight mainLight;
	private World world;
	private RayHandler rayHandler;
	private EntityPresets presets0 = new EntityPresets();
	private Array<Spawn> spawns = new Array<>();
	private GameHolder game = GameHolder.getInstance();
	
	public EntityManager(World world, RayHandler rayHandler) {
		this.world = world;
		this.rayHandler = rayHandler;
		instance = this;
	}
	
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
			e.printStackTrace();
		}
	}
	
	public void addCharacter(CharacterEntity character) {
		characters.add(character);
		game.environment.map.objects.add(new MapEntity(character));
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
			CharacterEntity bot = presets0.getRandomCharacter()
				.setBrain(new BotBrain(this))
				.create(world);
			addCharacter(bot);
		}
		return this;
	}
	
	public EntityManager addPresets(EntityPresets presets) {
		this.presets0 = presets;
		return this;
	}
	
	public EntityManager setSpawnsPositions(Array<Spawn> spawns) {
		this.spawns = spawns;
		return this;
	}
	
	public void render(SpriteBatch batch) {
		for(CharacterEntity entity : characters) {
			entity.drawProjectiles(batch);
		}
	}
	
	public Array<CharacterEntity> getAllCharacters() {
		Array<CharacterEntity> entities = new Array<>();
		for(CharacterEntity entity : characters) {
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