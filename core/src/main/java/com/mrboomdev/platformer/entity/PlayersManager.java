package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.entity.PlayerEntity;
import java.util.HashMap;

public class PlayersManager {
    private World world;
    private HashMap<String, PlayerEntity> players = new HashMap<>();
    
    public PlayersManager(World world) {
        this.world = world;
    }
    
    public void move(String nick, Vector2 power) {
        players.compute(nick, (String id, PlayerEntity player) -> {
            return player.move(power);
        });
    }
    
    public Vector2 getPosition(String nick) {
        return players.get(nick).body.getPosition();
    }
    
    public void add(String nick, PlayerEntity player) {
        players.put(nick, player);
    }
    
    public void remove(String nick) {
        players.remove(nick);
    }
    
    public void render(SpriteBatch batch) {
        for(PlayerEntity player : players.values()) {
            player.draw(batch);
        }
    }
}