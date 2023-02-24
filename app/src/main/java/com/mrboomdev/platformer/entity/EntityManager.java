package com.mrboomdev.platformer.entity;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.mrboomdev.platformer.entity.bot.BotBrain;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.MapManager;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collection;

public class EntityManager {
    private World world;
	private RayHandler rayHandler;
	private PointLight mainLight;
	private EntityPresets presets = new EntityPresets();
	private Array<Spawn> spawns = new Array<>();
    public static final String entitiesDirectory = "world/player/characters/";
	public HashMap<String, CharacterEntity> characters = new HashMap<>();
	
	public EntityManager(World world, RayHandler rayHandler) {
        this.world = world;
		this.rayHandler = rayHandler;
    }
	
	public void addCharacter(CharacterEntity character) {
		character.body.setTransform(getFreeSpawn(), 0);
        characters.put(character.name, character);
	}
	
	public void setMain(CharacterEntity character) {
		if(mainLight == null) {
			mainLight = new PointLight(rayHandler, 100, new Color(10, 40, 250, .7f), 10, 0, 0);
			mainLight.setSoftnessLength(1);
			mainLight.setContactFilter(Entity.LIGHT, Entity.NONE, Entity.BLOCK);
		}
		mainLight.attachToBody(character.body);
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
            entity.draw(batch);
        }
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