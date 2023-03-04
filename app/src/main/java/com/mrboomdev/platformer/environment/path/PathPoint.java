package com.mrboomdev.platformer.environment.path;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PathPoint {
	public Vector2 position;
	public int index = 0;
	private static int total = 0;
	
	public PathPoint(Vector2 position) {
		this.position = position;
		this.index = total++;
	}
	
	public void draw(SpriteBatch batch, boolean isActive) {
		
	}
}