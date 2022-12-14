package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.entity.Entity;

public class PlayerEntity extends Entity {
    private String nick;
    
    public PlayerEntity(String nick, World world) {
        super(world);
        this.nick = nick;
    }
    
    public PlayerEntity move(Vector2 power) {
        body.setLinearVelocity(power.x, power.y);
        return this;
    }
    
    public PlayerEntity setController() {
        return this;
    }
    
    public void draw(SpriteBatch batch) {
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
        sprite.draw(batch);
    }
}