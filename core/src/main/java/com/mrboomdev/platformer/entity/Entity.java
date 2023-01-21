package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.Gson;
import com.mrboomdev.platformer.entity.data.PlayerConfigData;
import com.mrboomdev.platformer.entity.EntityConfig.Stats;

public abstract class Entity {
    private World world;
    public boolean isDead, isDestroyed, isProjectile;
    public Controller controller;
    public Body body;
    public PlayerConfigData config;
    public EntityConfig configNew;
    public Stats stats;
    public String character;
    
    public Entity(String character, World world) {
        this(character, world, new Vector2(0, 0));
    }
    
    public Entity(String character, World world, Vector2 position) {
        Gson gson = new Gson();
        this.world = world;
        this.character = character;
        this.configNew = gson.fromJson(Gdx.files.internal(character + "/manifest.json").readString(), 
            EntityConfig.class).build(character, world);
        this.stats = configNew.stats.cpy();
        
        
        config = gson.fromJson(
            Gdx.files.internal(character + "/config.json").readString(), 
            PlayerConfigData.class).init();
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x, position.y);
        body = world.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.9f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }
    
    public void usePower(Vector2 power) {
        if(isDead) return;
        float speed = Math.max(Math.abs(power.x), Math.abs(power.y));
        //configNew.animation.setAnimation((speed > 3) ? "run" : "walk");
        body.setLinearVelocity(power.limit(5));
    }
    
    public void setController(Controller controller) {
        this.controller = controller;
    }
    
    public void gainDamage(int damage) {
        stats.health -= damage;
        if(stats.health <= 0) die();
    }
    
    public void die() {
        if(isDead) return;
        isDead = true;
    }
    
    public void draw(SpriteBatch batch) {
        if(isDead) return;
        configNew.body.direction.setFrom(body.getLinearVelocity().x);
        configNew.body.draw(batch, body.getPosition());
    }
    
    public void attack(Vector2 power) {
        throw new RuntimeException("Method not done yet.");
    }
    
    public void setPosition(Vector2 position) {
        if(isDead) return;
        body.setTransform(position, 0);
    }
}