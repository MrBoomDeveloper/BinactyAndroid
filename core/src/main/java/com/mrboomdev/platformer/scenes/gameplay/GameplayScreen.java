package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mrboomdev.platformer.entity.EntityColission;
import com.mrboomdev.platformer.entity.PlayerEntity;
import com.mrboomdev.platformer.environment.MapLayer;
import com.mrboomdev.platformer.environment.MapManager;
import com.mrboomdev.platformer.entity.PlayersManager;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.util.SizeUtil.Bounds;

public class GameplayScreen extends CoreScreen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private MapManager map;
    private PlayersManager players;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private GameplayUi ui;

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
            
        map.render(batch, MapLayer.BACKGROUND);
        map.render(batch, MapLayer.FOREGROUND);
        players.render(batch);
    	ui.render(delta);
        //debugRenderer.render(world, camera.combined);
        ui.debugValues.setValue("RenderCalls", String.valueOf(batch.renderCalls));

        batch.end();
        camera.position.set(players.getPosition("MrBoomDev"), 0);
        world.step(1 / 60f, 6, 2);
    }

    @Override
    public void dispose() {
	    ui.dispose();
    }

    @Override
    public void show() {
	    Box2D.init();
        debugRenderer = new Box2DDebugRenderer();
        
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new EntityColission());
        camera = new OrthographicCamera(32, 18);
        batch = new SpriteBatch();
    
        ui = new GameplayUi();
        Gdx.input.setInputProcessor(ui.stage);
        
        map = new MapManager();
        map.load(Gdx.files.internal("world/maps/test_01.json"));
        map.build(world);
        map.setCamera(camera);

        players = new PlayersManager(world);
        String[] nicks = {"MrBoomDev", "Kapusta", "FreddyFazbear123", "CatOMan", "Amogus"};
        for(String nick : nicks) {
            players.add(nick, new PlayerEntity(nick, world));
        }
        players.setController("MrBoomDev", ui.joystick);
        
        Music lobbyTheme = Gdx.audio.newMusic(Gdx.files.internal("audio/music/lobby_theme.mp3"));
        lobbyTheme.setVolume(.2f);
        lobbyTheme.setLooping(true);
        lobbyTheme.play();
    }
}