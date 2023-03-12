package com.mrboomdev.platformer.environment.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class MapObject implements Comparable<MapObject> {
	
	public abstract void draw(SpriteBatch batch);
	
	public abstract void setPosition(Vector2 position);
	
	public abstract Vector2 getPosition();
	
	public abstract int getLayer();
	
	public abstract void remove();
	
	@Override
    public int compareTo(MapObject object) {
		if(getLayer() != object.getLayer()) {
			return getLayer() - object.getLayer();
		} else if(getPosition().y != object.getPosition().y) {
			return Math.round(object.getPosition().y - getPosition().y);
		}
		return Math.round(getPosition().x - object.getPosition().x);
	}
}