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
import com.mrboomdev.platformer.entity.Controller;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.environment.MapManager;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collection;
import com.mrboomdev.platformer.entity.PlayerEntity;
import com.mrboomdev.platformer.entity.bot.BotEntity;

public class EntityManager {
    private World world;
    private Array<Vector2> spawns = new Array<>();
    public static final String entitiesDirectory = "world/player/characters/";
    public HashMap<String, Entity> entities = new HashMap<>();
    
    public EntityManager(World world) {
        this.world = world;
    }
    
    public void addPlayer(PlayerEntity player, Controller controller) {
        //TODO
    }
    
    public void addPlayer(PlayerEntity player, RayHandler rayHandler, Camera camera) {
        player.setPosition(spawns.get((int) (Math.random() * spawns.size)));
        player.camera = camera;
        PointLight playerLight = new PointLight(rayHandler, 100, new Color(10, 40, 250, .7f), 10, 0, 0);
        playerLight.attachToBody(player.body);
        entities.put(player.nick, player);
    }
    
    public void addBots(int count) {
        Gson gson = new Gson();
        String[] botsNicknames = gson.fromJson(Gdx.files.internal("world/player/bots.json").readString(), String[].class);
        HashSet<String> bots = new HashSet<>();
        while(bots.size() < count) {
            bots.add(botsNicknames[(int) (Math.random() * botsNicknames.length)]);
        }
        for(String name : bots) {
            BotEntity bot = new BotEntity(name, entitiesDirectory + "klarrie", world);
            bot.setPosition(spawns.get((int) (Math.random() * spawns.size)));
            entities.put(bot.nick, bot);
        }
    }
    
    public void setSpawnsPositions(Array<Vector2> spawns) {
        this.spawns = spawns;
    }
    
    public void render(SpriteBatch batch, OrthographicCamera camera) {
        for(Entity entity : entities.values()) {
            entity.draw(batch);
            if(entity.isDead && !entity.isDestroyed) {
                world.destroyBody(entity.body);
                entity.isDestroyed = true;
            }
        }
        
        batch.setProjectionMatrix(batch.getProjectionMatrix().cpy().scale(.02f, .02f, 1));
        for(Entity entity : entities.values()) {
            PlayerEntity player = (PlayerEntity)entity;
            player.drawNick(batch);
        }
        batch.setProjectionMatrix(camera.combined);
    }
    
    public void doAiStuff(PlayerEntity player, MapManager map) {
        for(Entity entity : entities.values()) {
            if(entity instanceof BotEntity)
                ((BotEntity)entity).doAiStuff(player, map);
        }
    }
    
    public Entity get(String name) {
        return entities.get(name);
    }
	
	public Collection<Entity> getAll() {
		return entities.values();
	}
}