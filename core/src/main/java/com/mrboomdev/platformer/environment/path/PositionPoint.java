package com.mrboomdev.platformer.environment.path;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class PositionPoint {
    public Vector2 position;
    public int index;

    public PositionPoint(Vector2 position) {
        this.position = position;
    }
    
    public PositionPoint(Vector2 position, int index) {
        this.position = position;
        this.index = index;
    }
    
    public void debug(ShapeRenderer renderer, boolean isActive) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(isActive ? Color.YELLOW : Color.RED);
        renderer.circle(position.x, position.y, 25);
        renderer.end();
    }
}