package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.math.Vector2;

public class MapData {
    public String name;
    public int width, height;
    public int version;
    String[] load;
    int[][] tiles;
    int[][] spawns;
    
    public Vector2 getSize() {
        return new Vector2(width, height);
    }
}