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
import com.mrboomdev.platformer.entity.data.PlayerConfigData;

public abstract class Entity {
    public Texture texture;
    public Controller controller;
    public Body body;
    public Sprite sprite;
    public PlayerConfigData config;
    //public Sprite weapon;
    
    public Entity(String name, World world, Vector2 position) {
        Gson gson = new Gson();
        config = gson.fromJson(
            Gdx.files.internal("world/player/characters/" + name + "/config.json").readString(), 
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
        body.setLinearVelocity(power.limit(5));
    }
    
    public void setController(Controller controller) {
        this.controller = controller;
    }
    
    public abstract void die();
    
    public void draw(SpriteBatch batch) {
        //TODO: add footstep sound
    };
}