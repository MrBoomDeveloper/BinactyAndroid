package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mrboomdev.platformer.environment.FreePosition;

public class FreePosition {
    public float x;
    public float y;
    public int index;
    
    public FreePosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void render(ShapeRenderer renderer, boolean isActive) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(isActive ? Color.BLUE : Color.YELLOW);
        renderer.circle(x, y, 25);
        renderer.end();
    }
}