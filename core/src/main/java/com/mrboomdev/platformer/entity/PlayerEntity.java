package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class PlayerEntity extends Entity {
    private String nick;
    private Controller controller;
    
    public PlayerEntity(String nick, World world) {
        super(world);
        this.nick = nick;
    }
    
    public void setController(Controller controller) {
        this.controller = controller;
    }
    
    public void draw(SpriteBatch batch) {
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
        sprite.draw(batch);
        
        if(controller != null) usePower(controller.getPower());
    }
    
    public void usePower(Vector2 power) {
        body.setLinearVelocity(power);
    }
}