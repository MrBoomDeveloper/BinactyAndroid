package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.environment.FreePosition;

public class TravelPath implements Connection<FreePosition> {
    private FreePosition fromPosition;
    private FreePosition toPosition;
    private float cost;
    
    public TravelPath(FreePosition fromPosition, FreePosition toPosition) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.cost = Vector2.dst(fromPosition.x, fromPosition.y, toPosition.x, toPosition.y);
    }
    
    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(new Color(255, 0, 0, .2f));
        renderer.rectLine(fromPosition.x, fromPosition.y, toPosition.x, toPosition.y, .1f);
        renderer.end();
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public FreePosition getFromNode() {
        return fromPosition;
    }

    @Override
    public FreePosition getToNode() {
        return toPosition;
    }
}