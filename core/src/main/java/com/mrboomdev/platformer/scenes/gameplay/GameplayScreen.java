package com.mrboomdev.platformer.scenes.gameplay;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
// import com.mrboomdev.platformer.util.anime.AnimeManual;
import com.badlogic.gdx.utils.ScreenUtils;
import com.google.gson.Gson;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.entity.EntityColission;
import com.mrboomdev.platformer.entity.PlayerEntity;
import com.mrboomdev.platformer.entity.PlayersManager;
import com.mrboomdev.platformer.environment.FreePosition;
import com.mrboomdev.platformer.environment.MapLayer;
import com.mrboomdev.platformer.environment.MapManager;
import com.mrboomdev.platformer.environment.PositionGraph;
import com.mrboomdev.platformer.environment.path.PositionPoint;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import java.util.HashSet;

public class GameplayScreen extends CoreScreen {
  private ShapeRenderer shapeRenderer;
  public OrthographicCamera camera;
  private SpriteBatch batch;
  private MapManager map;
  private PlayersManager players;
  private World world;
  private GameplayUi ui;
  private RayHandler rayHandler;
  private Box2DDebugRenderer debugRenderer;
  // private PositionGraph positionGraph;
  // private GraphPath<FreePosition> graphPath;
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
    players.render(batch);
    batch.end();

    rayHandler.setCombinedMatrix(camera);
    rayHandler.updateAndRender();

    batch.begin();
    // debugRenderer.render(world, camera.combined);
    players.drawNicks(batch, camera);
    //map.renderDebug(shapeRenderer);
    ui.render(delta);
    
    Vector2 myPos = players.getPosition("MrBoomDev");
    ui.debugValues.setValue("MyPositionX", String.valueOf(myPos.x / 2));
    ui.debugValues.setValue("MyPositionY", String.valueOf(myPos.y / 2));
    doAiShit(myPos);
    
    batch.end();

    camera.position.set(players.getPosition("MrBoomDev"), 0);
    world.step(Math.min(delta, 1 / 60f), 6, 2);
  }
  
  private void doAiShit(Vector2 myPos) {
    try {ui.debugValues.setValue("MrBoomDevPos", map.aiZones.get(PositionPoint.toText(players.getPosition("MrBoomDev"))).position.toString());} catch(Exception e) {e.printStackTrace();}
    for(PlayerEntity bot : players.getAll()) {
      if(bot.nick == "MrBoomDev") continue;
      
      Vector2 botPosition = bot.body.getPosition();
      Vector2 power = players.getPosition("MrBoomDev").sub(botPosition);
      if(Math.abs(power.x) > 6 || Math.abs(power.y) > 6) {
        power = new Vector2(0, 0);
      }
      
      /*String botTextPosition = PositionPoint.toText(botPosition);
      if(map.aiZones.containsKey(botTextPosition)) {
          String playerTextPosition = PositionPoint.toText(players.getPosition("MrBoomDev"));
          if(map.aiZones.containsKey(playerTextPosition)) {
              GraphPath<FreePosition> path = map.positionGraph.findPath(
                  map.aiZones.get(playerTextPosition), map.aiZones.get(botTextPosition));
              for(FreePosition position : path) {
                  position.render(shapeRenderer, true);
              }
              try {
                  path.get(1).draw(shapeRenderer, new Color(255, 255, 0, 1));
                  ui.debugValues.setValue("botPosFrom", botTextPosition);
                  ui.debugValues.setValue("botPowerSuper", PositionPoint.toText(path.get(1).position.sub(botPosition).scl(10).limit(4)));
                  ui.debugValues.setValue("botPowerNoLimit", PositionPoint.toText(path.get(1).position.sub(botPosition).scl(10)));
                  ui.debugValues.setValue("botPowerJust", PositionPoint.toText(path.get(1).position.sub(botPosition)));
                  ui.debugValues.setValue("botPosTo", PositionPoint.toText(path.get(1).position));
                  bot.body.setLinearVelocity(path.get(1).position.sub(botPosition).scl(10).limit(4));
              } catch(Exception e) {
                  e.printStackTrace();
              }
              continue;
          } else {
              ui.debugValues.setValue("player pos", playerTextPosition);
              Gdx.app.log("Bot " + bot.nick, "Cannot walk by using pathfinding, because of player");
          }
      } else {
          Gdx.app.log("Bot " + bot.nick, "Cannot walk by using pathfinding, because of bot");
      }*/
      
      bot.body.setLinearVelocity(power.scl(2).limit(3));
    }
  }

  @Override
  public void show() {
    batch = new SpriteBatch();
    Box2D.init();

    // anime = new AnimeManual();
    shapeRenderer = new ShapeRenderer();
    // anime.addEntity(shapeRenderer);
    /*anime.setUpdateListener((ShapeRenderer shapeRenderer) -> {

    });*/

    world = new World(new Vector2(0, 0), true);
    world.setContactListener(new EntityColission());
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

    players = new PlayersManager(world);
    Gson gson = new Gson();
    String[] botsNicks =
        gson.fromJson(Gdx.files.internal("world/player/bots.json").readString(), String[].class);
    HashSet<String> nicks = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      nicks.add(botsNicks[(int) (Math.random() * botsNicks.length)]);
    }
    nicks.add("MrBoomDev");
    Array<Vector2> spawnPositions = map.spawnPositions;
    for (String nick : nicks) {
      players.add(nick, new PlayerEntity("klarrie", nick, world,
        spawnPositions.get((int) (Math.random() * spawnPositions.size))));
    }
    players.setController("MrBoomDev", ui.joystick);

    PointLight playerLight = new PointLight(rayHandler, 100, new Color(10, 40, 250, .7f), 10, 0, 0);
    playerLight.attachToBody(players.getBody("MrBoomDev"));

    Music lobbyTheme = MainGame.getInstance().asset.get("audio/music/lobby_theme.mp3", Music.class);
    lobbyTheme.setVolume(.2f);
    lobbyTheme.setLooping(true);
    lobbyTheme.play();

    debugRenderer = new Box2DDebugRenderer();
  }

  @Override
  public void dispose() {
    shapeRenderer.dispose();
    rayHandler.dispose();
    ui.dispose();
  }
}