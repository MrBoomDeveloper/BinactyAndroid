package com.mrboomdev.platformer.environment;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mrboomdev.platformer.environment.gamemode.GamemodeManager;
import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.ui.gameplay.GameplayUi;
import com.mrboomdev.platformer.util.CameraUtil;

public class EnvironmentManager {
	public MapManager map;
	public OrthographicCamera camera;
	public GamemodeManager gamemode;
	public RayHandler rayHandler;
	public World world;
	public Stage stage;
	public GameplayUi ui;
	private GameHolder game = GameHolder.getInstance();
	
	public EnvironmentManager() {
		Box2D.init();
		world = new World(new Vector2(0, 0), true);
		GameHolder.getInstance().environment = this;
	}
	
	public void render(SpriteBatch batch) {
		map.render(batch);
		//stage.draw();
	}
	
	public void update(float delta) {
		stage.act(delta);
		world.step(Math.min(delta, 1 / 60f), 6, 2);
		gamemode.update();
		CameraUtil.update(delta);
	}
	
	public void start(Stage stage) {
		this.stage = stage;
		ui = new GameplayUi();
		ui.createFreeRoam(stage);
		if(!game.settings.enableEditor) ui.createCombat(stage);
		if(game.settings.enableEditor) ui.createEditor(stage);
		gamemode.createUi(stage);
		ui.connectCharacter(game.settings.mainPlayer);
	}
	
	public void setupRayHandler() {
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(map.atmosphere.environmentLightColor.getColor());
		rayHandler.setBlurNum(1);
		map.rayHandler = rayHandler;
		
		for(var object : map.objects) {
			if(!(object instanceof MapTile)) continue;
			((MapTile)object).setupRayHandler(rayHandler);
		}
	}
}