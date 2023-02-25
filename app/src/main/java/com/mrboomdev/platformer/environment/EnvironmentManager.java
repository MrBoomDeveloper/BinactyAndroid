package com.mrboomdev.platformer.environment;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.environment.EnvironmentMap;
import com.mrboomdev.platformer.environment.gamemode.GamemodeManager;
import com.mrboomdev.platformer.scenes.core.CoreUi;

public class EnvironmentManager {
	public EnvironmentMap map;
	public GamemodeManager gamemode;
	public RayHandler rayHandler;
	public World world;
	
	public EnvironmentManager() {
		Box2D.init();
		world = new World(new Vector2(0, 0), true);
	}
	
	public void render(SpriteBatch batch) {
		map.render(batch);
	}
	
	public void attachUi(CoreUi ui) {
		ui.attachLayerDrawer(gamemode);
	}
	
	public void setupRayHandler() {
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0, 0, 0, .1f);
		rayHandler.setBlurNum(3);
		
		for(EnvironmentMap.Tile tile : map.tiles) {
			tile.block.setupRayHandler(rayHandler);
		}
	}
}