package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.environment.EnvironmentMap;

public class EnvironmentManager {
	public EnvironmentMap map;
	public World world;
	
	public EnvironmentManager() {
		Box2D.init();
		world = new World(new Vector2(0, 0), true);
	}
	
	public void render(SpriteBatch batch) {
		map.render(batch);
	}
	
	public void fireListener() {
		
	}
}