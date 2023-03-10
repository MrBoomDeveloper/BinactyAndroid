package com.mrboomdev.platformer.environment.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface MapObject<T> extends Comparable<T> {
	
	public void draw(SpriteBatch batch);
	
	public void setPosition(Vector2 position);
	
	public Vector2 getPosition();
	
	public void remove();
}