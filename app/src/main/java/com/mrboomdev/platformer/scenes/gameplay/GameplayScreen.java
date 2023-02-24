package com.mrboomdev.platformer.scenes.gameplay;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.EntityPresets;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.EnvironmentManager;
import com.mrboomdev.platformer.environment.MapLayer;
import com.mrboomdev.platformer.environment.MapManager;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.projectile.ProjectileColission;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.util.CameraUtil;

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
			map.render(batch, MapLayer.BACKGROUND);
			map.render(batch, MapLayer.FOREGROUND);
			environment.render(batch);
			entities.render(batch, camera);
		}
		batch.end();
		rayHandler.setCombinedMatrix(camera);
		rayHandler.updateAndRender();
		batch.begin();
		{
			if(game.settings.debugRenderer) {
				debugRenderer.render(environment.world, camera.combined);
			}
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
		environment.world.step(Math.min(delta, 1 / 60f), 6, 2);
		CameraUtil.update(delta);
	}
	
	@Override
	public void show() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		debugRenderer = new Box2DDebugRenderer();
		
		environment.world.setContactListener(new ProjectileColission());
		rayHandler = new RayHandler(environment.world);
		rayHandler.setAmbientLight(0, 0, 0, .1f);
		rayHandler.setBlurNum(3);
		camera = new OrthographicCamera(32, 18);
		
		map = new MapManager();
		map.load(Gdx.files.internal("world/maps/test_01.json"));
		map.build(environment.world, rayHandler);
		map.setCamera(camera);
		
		entities = new EntityManager(environment.world, rayHandler)
			.setSpawnsPositions(map.spawnPositions)
			.addPresets(EntityPresets.getInternal())
			.addBots(5);
		
		CharacterEntity player = new CharacterEntity(game.settings.playerName)
			.setConfigFromJson(Entity.getInternal(Entity.CHARACTER, "klarrie","manifest.json").readString())
			.create(environment.world);
		
		entities.addCharacter(player);
		entities.setMain(player);
		camera.position.set(player.body.getPosition(), 0);
		
		ui = new GameplayUi(this, player);
		Gdx.input.setInputProcessor(ui.stage);
		
		Music lobbyTheme = game.assets.get("audio/music/lobby_theme.mp3", Music.class);
		lobbyTheme.setVolume(.2f * game.settings.musicVolume / 100);
		lobbyTheme.setLooping(true);
		lobbyTheme.play();
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
		rayHandler.dispose();
		ui.dispose();
	}
}