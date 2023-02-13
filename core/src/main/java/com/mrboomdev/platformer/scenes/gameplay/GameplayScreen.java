package com.mrboomdev.platformer.scenes.gameplay;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
//import com.crashinvaders.vfx.effects.
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.bot.BotBrain;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.MapLayer;
import com.mrboomdev.platformer.environment.MapManager;
import com.mrboomdev.platformer.projectile.ProjectileColission;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.util.CameraUtil;
//import com.crashinvaders.vfx.VfxManager;
// import com.mrboomdev.platformer.util.anime.AnimeManual;

public class GameplayScreen extends CoreScreen {
	public OrthographicCamera camera;
	public EntityManager entities;
	private MainGame game;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private MapManager map;
	private World world;
	private GameplayUi ui;
	private RayHandler rayHandler;
	private Box2DDebugRenderer debugRenderer;
	//private VfxManager vfx;
	// private AnimeManual anime;
	
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		batch.begin();
		map.render(batch, MapLayer.BACKGROUND);
		map.render(batch, MapLayer.FOREGROUND);
		entities.render(batch, camera);
		batch.end();
		
		rayHandler.setCombinedMatrix(camera);
		rayHandler.updateAndRender();
		
		batch.begin();
		if(game.showBodyColissions) debugRenderer.render(world, camera.combined);
		//map.renderDebug(shapeRenderer);
		ui.render(delta);
		batch.end();
		
		if(!entities.getCharacter(game.nick).isDead) {
			Vector2 playerPosition = entities.getCharacter(game.nick).body.getPosition();
			Vector2 position = new Vector2(camera.position.x, camera.position.y);
			position.x += (playerPosition.x - position.x) * .1f;
			position.y += (playerPosition.y - position.y) * .1f;
			camera.position.set(position.add(CameraUtil.getCameraShake()), 0);
		}
		world.step(Math.min(delta, 1 / 60f), 6, 2);
		CameraUtil.update(delta);
	}
	
	@Override
	public void show() {
		this.game = MainGame.getInstance();
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		Box2D.init();
		
		//vfx = new VfxManager(Pixmap.Format.RGBA8888);
		
		// anime = new AnimeManual();
		// anime.addEntity(shapeRenderer);
		/*anime.setUpdateListener((ShapeRenderer shapeRenderer) -> {
			
		});*/
		
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(new ProjectileColission());
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0, 0, 0, .1f);
		rayHandler.setBlurNum(3);
		camera = new OrthographicCamera(32, 18);
		
		map = new MapManager();
		map.load(Gdx.files.internal("world/maps/test_01.json"));
		map.build(world, rayHandler);
		map.setCamera(camera);
		
		entities = new EntityManager(world, rayHandler);
		entities.setSpawnsPositions(map.spawnPositions);
		entities.addBots(game.botsCount);
		
		CharacterEntity player = new CharacterEntity(game.nick)
			.setConfigFromJson(Entity.getInternal(Entity.CHARACTER, "klarrie","manifest.json").readString())
			.create(world);
		
		entities.addCharacter(player);
		entities.setMain(player);
		camera.position.set(player.body.getPosition(), 0);
		
		ui = new GameplayUi(this, player);
		Gdx.input.setInputProcessor(ui.stage);
		
		Music lobbyTheme = MainGame.getInstance().asset.get("audio/music/lobby_theme.mp3", Music.class);
		lobbyTheme.setVolume(.2f);
		lobbyTheme.setLooping(true);
		lobbyTheme.play();
		
		if(game.showBodyColissions) debugRenderer = new Box2DDebugRenderer();
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
		rayHandler.dispose();
		ui.dispose();
	}
}