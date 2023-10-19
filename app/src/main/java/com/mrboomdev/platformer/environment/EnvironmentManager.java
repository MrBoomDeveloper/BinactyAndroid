package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.particle.ParticleManager;
import com.mrboomdev.platformer.environment.gamemode.GamemodeManager;
import com.mrboomdev.platformer.environment.logic.Trigger;
import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.ui.gameplay.GameplayUi;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.FunUtil;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;

import box2dLight.RayHandler;

public class EnvironmentManager {
	public MapManager map;
	public OrthographicCamera camera;
	public GamemodeManager gamemode;
	public RayHandler rayHandler;
	public World world;
	public Stage stage;
	public GameplayUi ui;
	public EntityManager entities;
	public ParticleManager particles;
	public SpriteBatch batch;
	public ShaderProgram shader;
	private final GameHolder game = GameHolder.getInstance();
	
	public EnvironmentManager() {
		FunUtil.timerTasks.clear();
		Box2D.init();
		world = new World(new Vector2(0, 0), true);

		var particlesDir = FileUtil.internal("packs/official/src/particles");
		particles = new ParticleManager();
		particles.loadParticle(particlesDir.goTo("dust"), "__dust");
		particles.loadParticle(particlesDir.goTo("tiny_boom"), "__tiny_boom");
		particles.loadParticle(particlesDir.goTo("medium_boom"), "__medium_boom");
	}
	
	public void render(SpriteBatch batch) {
		if(map != null) map.render(batch);
		//entities.render(batch);
		particles.draw(batch);
		//stage.draw();
	}
	
	public void update(float delta) {
		if(map != null) map.ping();
		//stage.act(delta);
		world.step(1 / 60f, 6, 2);
		//gamemode.update();

		CameraUtil.update(delta);
		Trigger.updateAll();
		FunUtil.update();
		AudioUtil.update();
	}
	
	public void start(Stage stage) {
		this.stage = stage;

		ui = new GameplayUi();
		ui.createFreeRoam(stage);

		if(!game.settings.enableEditor) ui.createCombat(stage);
		if(game.settings.enableEditor) ui.createEditor(stage);

		//ui.connectCharacter(game.settings.mainPlayer);
		gamemode.createUi(stage);
		game.script.bridge.triggerStarted();
	}
	
	public void setupRayHandler() {
		rayHandler = new RayHandler(world);
		rayHandler.setBlurNum(3);
		rayHandler.setCulling(true);

		if(game.settings.enableEditor) rayHandler.setShadows(false);
		else rayHandler.setAmbientLight(map.atmosphere.environmentLightColor.getColor());

		map.rayHandler = rayHandler;
		entities.setupRayHandler(rayHandler);
		
		for(var object : map.objects) {
			if(!(object instanceof MapTile)) continue;
			((MapTile)object).setupRayHandler(rayHandler);
		}
	}
}