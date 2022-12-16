package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.entity.Controller;
import com.mrboomdev.platformer.entity.PlayerEntity;
import java.util.HashMap;

public class PlayersManager {
    private World world;
    private PlayerEntity[] playersArray = new PlayerEntity[10];
    private HashMap<String, Integer> playersPositions = new HashMap<>();
    
    public PlayersManager(World world) {
        this.world = world;
    }
    
    public void setController(String nick, Controller controller) {
        playersArray[playersPositions.get(nick)].setController(controller);
    }
    
    public Vector2 getPosition(String nick) {
        return playersArray[playersPositions.get(nick)].body.getPosition();
    }
    
    public void add(String nick, PlayerEntity player) {
        int id = playersPositions.size();
        playersPositions.put(nick, id);
        playersArray[id] = player;
    }
    
    public void remove(String nick) {
        throw new RuntimeException("Not available currently");
    }
    
    public void render(SpriteBatch batch) {
        for(int i = 0; i < playersPositions.size(); i++) {
            try {
            playersArray[i].draw(batch);
            } catch(Exception e) { e.printStackTrace(); }
        }
    }
}