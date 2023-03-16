package com.mrboomdev.platformer.scenes.gameplay;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.EntityPresets;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.EnvironmentManager;
import com.mrboomdev.platformer.environment.MapManager;
import com.mrboomdev.platformer.environment.editor.EditorManager;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.projectile.ProjectileColission;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.FileUtil;

public class GameplayScreen extends CoreScreen {
	public OrthographicCamera camera;
	public EntityManager entities;
	public EnvironmentManager environment;
	private GameHolder game;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private MapManager map;
	private GameplayUi ui;
	private RayHandler rayHandler;
	private Box2DDebugRenderer debugRenderer;
	
	public GameplayScreen(EnvironmentManager manager) {
		this.environment = manager;
		this.game = GameHolder.getInstance();
	}
	
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		batch.begin();
		{
			map.render(batch);
			environment.render(batch);
			entities.render(batch, camera);
		}
		batch.end();
		if(!game.settings.debugRaysDisable) {
			rayHandler.setCombinedMatrix(camera);
			rayHandler.updateAndRender();
		}
		batch.begin();
		{
			if(game.settings.debugRenderer)
				debugRenderer.render(environment.world, camera.combined);
			ui.render(delta);
		}
		batch.end();
		String playerName = game.settings.playerName;
		if(!entities.getCharacter(playerName).isDead) {
			Vector2 playerPosition = entities.getCharacter(playerName).body.getPosition();
			camera.position.set(CameraUtil.getCameraShake().add(
				camera.position.x + (playerPosition.x - camera.position.x) * .1f,
				camera.position.y + (playerPosition.y - camera.position.y) * .1f
			), 0);
		}
		environment.update(delta);
	}
	
	@Override
	public void show() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		debugRenderer = new Box2DDebugRenderer();
		
		environment.world.setContactListener(new ProjectileColission());
		environment.setupRayHandler();
		rayHandler = environment.rayHandler;
		camera = new OrthographicCamera(32, 18);
		environment.camera = camera;
		
		map = new MapManager();
		map.load(Gdx.files.internal("world/maps/test_01.json"));
		map.build(environment.world, rayHandler);
		map.setCamera(camera);
		
		entities = new EntityManager(environment.world, rayHandler)
			.setSpawnsPositions(map.spawnPositions)
			.addPresets(EntityPresets.getInternal())
			.addBots(5);
		
		CharacterEntity player = new CharacterEntity(game.settings.playerName)
			.setConfig(new FileUtil(
				EntityManager.entitiesDirectory + "klarrie",
				FileUtil.Source.INTERNAL))
			.create(environment.world);
		
		game.settings.mainPlayer = player;
		entities.addCharacter(player);
		entities.setMain(player);
		camera.position.set(player.body.getPosition(), 0);
		
		ui = new GameplayUi(this, player);
		Gdx.input.setInputProcessor(ui.stage);
		if(game.settings.enableEditor) {
			EditorManager editor = new EditorManager();
			ui.attachLayerDrawer(editor);
		}
		environment.attachUi(ui);
		environment.start(ui.stage);
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
		rayHandler.dispose();
		ui.dispose();
	}
}