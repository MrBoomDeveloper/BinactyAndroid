package com.mrboomdev.platformer.environment.map;

import androidx.annotation.NonNull;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class MapObject implements Comparable<MapObject> {
	
	public abstract void draw(SpriteBatch batch);
	public abstract void setPosition(Vector2 position);
	public abstract Vector2 getPosition(boolean isBottom);
	public abstract int getLayer();
	public abstract void remove();
	
	@Override
    public int compareTo(@NonNull MapObject object) {
		if(getLayer() != object.getLayer()) {
			return getLayer() - object.getLayer();
		} else if(getPosition(true).y != object.getPosition(true).y) {
			return (object.getPosition(true).y > getPosition(true).y) ? 1 : -1;
		}
		return (object.getPosition(true).x > getPosition(true).x) ? 1 : -1;
	}
}