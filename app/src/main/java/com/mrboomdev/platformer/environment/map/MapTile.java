package com.mrboomdev.platformer.environment.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.google.gson.annotations.SerializedName;

public class MapTile implements MapObject<MapObject> {
	public String name;
	private int layer;
    private Body body;
	
	@SerializedName("texture")
	public String texturePath;
	
	@Override
	public void draw(SpriteBatch batch) {
		
	}

    @Override
    public void setPosition(Vector2 position) {
        body.setTransform(position, 0);
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public void remove() {}

    @Override
    public int compareTo(MapObject object) {
		return Math.round(object.getPosition().x - getPosition().x);
	}
}