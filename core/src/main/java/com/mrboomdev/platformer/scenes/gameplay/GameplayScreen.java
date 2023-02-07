package com.mrboomdev.platformer.scenes.gameplay;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
// import com.mrboomdev.platformer.util.anime.AnimeManual;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.PlayerEntity;
import com.mrboomdev.platformer.environment.MapLayer;
import com.mrboomdev.platformer.environment.MapManager;
import com.mrboomdev.platformer.projectile.ProjectileColission;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.util.CameraUtil;

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
    
    if(!entities.get(game.nick).isDead) {
        camera.position.set(entities.get(game.nick).body.getPosition().add(CameraUtil.getCameraShake()), 0);
    }
    world.step(Math.min(delta, 1 / 60f), 6, 2);
    entities.doAiStuff((PlayerEntity)entities.get(game.nick), map);
    CameraUtil.update(delta);
  }
  
  @Override
  public void show() {
    this.game = MainGame.getInstance();
    batch = new SpriteBatch();
    shapeRenderer = new ShapeRenderer();
    Box2D.init();

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
    
    entities = new EntityManager(world);
    entities.setSpawnsPositions(map.spawnPositions);
    entities.addBots(game.botsCount);
    PlayerEntity player = new PlayerEntity(game.nick, EntityManager.entitiesDirectory + "klarrie", world);
    entities.addPlayer(player, rayHandler, camera);
        
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