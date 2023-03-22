package com.mrboomdev.platformer.environment;

import box2dLight.RayHandler;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Block {
    private static final float tileSize = 2;
    public String special;
    
    public String build(Vector2 position, World world, RayHandler rayHandler) {
        return special;
    }
}