package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class FreePosition {
    public float x;
    public float y;
    public int index;
    public Vector2 position;
    
    public FreePosition(float x, float y) {
        this.position = new Vector2(x, y);
        this.x = x;
        this.y = y;
    }
    
    public void render(ShapeRenderer renderer, boolean isActive) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(isActive ? new Color(255, 0, 0, .5f) : new Color(0, 0, 255, .5f));
        renderer.circle(x, y, .4f);
        renderer.end();
    }
    
    public void draw(ShapeRenderer renderer, Color color) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(color);
        renderer.circle(x, y, .5f);
        renderer.end();
    }
}