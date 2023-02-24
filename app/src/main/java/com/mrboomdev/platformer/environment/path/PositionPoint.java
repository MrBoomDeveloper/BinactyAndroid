package com.mrboomdev.platformer.environment.path;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class PositionPoint {
    public Vector2 position;
    public int index;
    
    public float getX() {
        return position.x;
    }
    
    public float getY() {
        return position.y;
    }

    public PositionPoint(Vector2 position) {
        this.position = position;
    }
    
    public PositionPoint(Vector2 position, int index) {
        this.position = position;
        this.index = index;
    }
    
    public static String toText(Vector2 position) {
        return (int)(position.x / 2) + "_" + (int)(position.y / 2); 
    }
    
    public static String toTextSimple(Vector2 position) {
        return (int)(position.x) + "_" + (int)(position.y); 
    }
    
    public void debug(ShapeRenderer renderer, boolean isActive) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(isActive ? Color.RED : Color.BLUE);
        renderer.circle(position.x, position.y, .5f);
        renderer.end();
    }
}