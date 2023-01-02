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
import com.mrboomdev.platformer.environment.FreePosition;
import com.mrboomdev.platformer.environment.PositionGraph;
import com.mrboomdev.platformer.environment.TravelPath;
import java.util.HashSet;
import com.badlogic.gdx.utils.ScreenUtils;
import com.google.gson.Gson;
import com.mrboomdev.platformer.entity.EntityColission;
import com.mrboomdev.platformer.entity.PlayerEntity;
import com.mrboomdev.platformer.environment.MapLayer;
import com.mrboomdev.platformer.environment.MapManager;
import com.mrboomdev.platformer.entity.PlayersManager;
import com.mrboomdev.platformer.scenes.core.CoreScreen;

public class GameplayScreen extends CoreScreen {
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private MapManager map;
    private PlayersManager players;
    private World world;
    private GameplayUi ui;
    private RayHandler rayHandler;
    private Box2DDebugRenderer debugRenderer;
    private PositionGraph positionGraph;
    private GraphPath<FreePosition> graphPath;

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        map.render(batch, MapLayer.BACKGROUND);
        map.render(batch, MapLayer.FOREGROUND);
        players.render(batch);
        batch.end();
        
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
        
        batch.begin();
        //debugRenderer.render(world, camera.combined);
        players.drawNicks(batch, camera);
        ui.render(delta);
        for(TravelPath path : positionGraph.paths) {
            path.render(shapeRenderer);
        }
        for(FreePosition position : positionGraph.positions) {
            position.render(shapeRenderer, false);
        }
        for(FreePosition position : graphPath) {
            position.render(shapeRenderer, true);
        }
        batch.end();
        
        camera.position.set(players.getPosition("MrBoomDev"), 0);
        world.step(1 / 60f, 6, 2);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
	    Box2D.init();
        
        shapeRenderer = new ShapeRenderer();
        positionGraph = new PositionGraph();
        
        FreePosition startPosition = new FreePosition(50, 50);
        FreePosition pos1 = new FreePosition(200, 200);
        FreePosition pos2 = new FreePosition(400, 600);
        FreePosition pos3 = new FreePosition(300, 700);
        positionGraph.addPosition(startPosition);
        positionGraph.addPosition(pos1);
        positionGraph.addPosition(pos2);
        positionGraph.addPosition(pos3);
        positionGraph.connectPositions(startPosition, pos1);
        positionGraph.connectPositions(pos1, pos2);
        positionGraph.connectPositions(startPosition, pos2);
        graphPath = positionGraph.findPath(startPosition, pos2);
        
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new EntityColission());
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0, 0, 0, .1f);
        rayHandler.setBlurNum(3);
        camera = new OrthographicCamera(32, 18);
    
        ui = new GameplayUi();
        Gdx.input.setInputProcessor(ui.stage);
        
        map = new MapManager();
        map.load(Gdx.files.internal("world/maps/test_01.json"));
        map.build(world, rayHandler);
        map.setCamera(camera);

        players = new PlayersManager(world);
        Gson gson = new Gson();
        String[] botsNicks = gson.fromJson(Gdx.files.internal("world/player/bots.json").readString(), String[].class);
        HashSet<String> nicks = new HashSet<>();
        for(int i = 0; i < 9; i++) {
            nicks.add(botsNicks[(int)(Math.random() * botsNicks.length)]);
        }
        nicks.add("MrBoomDev");
        for(String nick : nicks) {
            players.add(nick, new PlayerEntity("klarrie", nick, world));
        }
        players.setController("MrBoomDev", ui.joystick);
        
        PointLight playerLight = new PointLight(
            rayHandler, 100,
            new Color(10, 40, 250, .7f),
            10, 0, 0);
        playerLight.attachToBody(players.getBody("MrBoomDev"));
        
        Music lobbyTheme = Gdx.audio.newMusic(Gdx.files.internal("audio/music/lobby_theme.mp3"));
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