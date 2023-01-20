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

public class GameplayScreen extends CoreScreen {
  private MainGame game;
  private SpriteBatch batch;
  private ShapeRenderer shapeRenderer;
  public OrthographicCamera camera;
  private MapManager map;
  private EntityManager entities;
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
    if(game.isDebug()) debugRenderer.render(world, camera.combined);
    //map.renderDebug(shapeRenderer);
    ui.render(delta);
    Vector2 myPos = entities.get("MrBoomDev").body.getPosition();
    ui.debugValues.setValue("MyPositionX", String.valueOf(myPos.x / 2));
    ui.debugValues.setValue("MyPositionY", String.valueOf(myPos.y / 2));
    ui.debugValues.setValue("MyHealth", String.valueOf(entities.get("MrBoomDev").stats.health));
    
    batch.end();
    
    if(!entities.get("MrBoomDev").isDead) {
        camera.position.set(entities.get("MrBoomDev").body.getPosition(), 0);
    }
    world.step(Math.min(delta, 1 / 60f), 6, 2);
    entities.doAiStuff((PlayerEntity)entities.get("MrBoomDev"), map);
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

    ui = new GameplayUi(this);
    Gdx.input.setInputProcessor(ui.stage);

    map = new MapManager();
    map.load(Gdx.files.internal("world/maps/test_01.json"));
    map.build(world, rayHandler);
    map.setCamera(camera);
    
    entities = new EntityManager(world);
    entities.setSpawnsPositions(map.spawnPositions);
    entities.addBots(10);
    entities.addPlayer(new PlayerEntity("MrBoomDev", "klarrie", world), ui.joystick, rayHandler);

    Music lobbyTheme = MainGame.getInstance().asset.get("audio/music/lobby_theme.mp3", Music.class);
    lobbyTheme.setVolume(.2f);
    lobbyTheme.setLooping(true);
    lobbyTheme.play();
    
    if(game.isDebug()) debugRenderer = new Box2DDebugRenderer();
  }

  @Override
  public void dispose() {
    shapeRenderer.dispose();
    rayHandler.dispose();
    ui.dispose();
  }
}