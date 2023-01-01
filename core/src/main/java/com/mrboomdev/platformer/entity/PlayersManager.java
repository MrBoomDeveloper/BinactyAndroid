package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.entity.Controller;
import com.mrboomdev.platformer.entity.PlayerEntity;
import java.util.HashMap;

public class PlayersManager {
    private World world;
    private HashMap<String, PlayerEntity> players = new HashMap<>();
    
    public PlayersManager(World world) {
        this.world = world;
    }
    
    public void setController(String nick, Controller controller) {
        players.get(nick).setController(controller);
    }
    
    public Vector2 getPosition(String nick) {
        return players.get(nick).body.getPosition();
    }
    
    public Body getBody(String nick) {
        return players.get(nick).body;
    }
    
    public void add(String nick, PlayerEntity player) {
        players.put(nick, player);
    }
    
    public void remove(String nick) {
        throw new RuntimeException("Not available currently");
    }
    
    public void render(SpriteBatch batch) {
        for(PlayerEntity player : players.values()) {
            player.draw(batch);
        }
    }
    
    public void drawNicks(SpriteBatch batch, Camera camera) {
        batch.setProjectionMatrix(batch.getProjectionMatrix().cpy().scale(.025f, .025f, 1));
        for(PlayerEntity player : players.values()) {
            if(player != null) {
                player.drawNick(batch);
            }
        }
        batch.setProjectionMatrix(camera.combined);
    }
}