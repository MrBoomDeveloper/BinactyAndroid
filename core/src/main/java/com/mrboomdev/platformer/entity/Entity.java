package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.Gson;
import com.mrboomdev.platformer.entity.EntityConfig;
import com.mrboomdev.platformer.entity.data.PlayerConfigData;
import com.mrboomdev.platformer.entity.EntityConfig.Stats;

public abstract class Entity {
    private World world;
    public static final String entitiesDirectory = "world/player/characters/";
    public boolean isDead, isDestroyed, isProjectile;
    public Texture texture;
    public Controller controller;
    public Body body;
    public Sprite sprite;
    public PlayerConfigData config;
    public EntityConfig configNew;
    public Stats stats;
    //public Sprite weapon;
    
    public Entity(String character, World world) {
        this(character, world, new Vector2(0, 0));
    }
    
    public Entity(String projectile, World world, Vector2 position, boolean isProjectile) {
        this.isProjectile = isProjectile;
    }
    
    public Entity(String character, World world, Vector2 position) {
        this.world = world;
        Gson gson = new Gson();
        configNew = gson.fromJson(Gdx.files.internal(entitiesDirectory + character + "/manifest.json").readString(), 
            EntityConfig.class).build(character, world);
        this.stats = configNew.stats.cpy();
        
        config = gson.fromJson(
            Gdx.files.internal(entitiesDirectory + character + "/config.json").readString(), 
            PlayerConfigData.class).init();
        
        /*weapon = new Sprite(new Texture(Gdx.files.internal("world/player/weapon/tomson.png")));
        weapon.setSize(1.2f, .6f);*/
    
	    sprite = new Sprite(new Texture(Gdx.files.internal("world/player/jack.png")));
	    sprite.setSize(1.0f, 1.8f);
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x, position.y);
        body = world.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() / 2, sprite.getHeight() / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }
    
    public void usePower(Vector2 power) {
        if(isDead) return;
        body.setLinearVelocity(power.limit(5));
    }
    
    public void setController(Controller controller) {
        this.controller = controller;
    }
    
    public void gainDamage(int damage) {
        stats.health -= damage;
        if(stats.health <= 0) {
            die();
        }
    }
    
    public void die() {
        if(isDead) return;
        isDead = true;
    }
    
    public void draw(SpriteBatch batch) {
        if(isDead) return;
        configNew.body.draw(batch, body.getPosition());
    }
    
    public void attack(Vector2 power) {
        
    }
    
    public void setPosition(Vector2 position) {
        if(isDead) return;
        body.setTransform(position, 0);
    }
}