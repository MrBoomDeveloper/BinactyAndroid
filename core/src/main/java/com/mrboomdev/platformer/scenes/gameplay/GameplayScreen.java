package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.Gdx;
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
        players.render(batch);
        //map.render(batch, MapLayer.FOREGROUND);
    	ui.render(delta);
        debugRenderer.render(world, camera.combined);

        batch.end();
        camera.position.set(players.getPosition("MrBoomDev"), 0);
        world.step(1 / 60f, 6, 2);
        ui.debugValues.setValue("RenderCalls", String.valueOf(batch.renderCalls));
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
        camera = new OrthographicCamera(32, 18);
        batch = new SpriteBatch();
    
        ui = new GameplayUi();
        Gdx.input.setInputProcessor(ui.stage);
        
        map = new MapManager();
        map.load(Gdx.files.internal("data/maps/test_04.json"));
        map.build(world);
        map.setCamera(camera);

        players = new PlayersManager(world);
        String[] nicks = {"MrBoomDev", "Kapusta", "FreddyFazbear123", "CatOMan", "Amogus"};
        for(String nick : nicks) {
            players.add(nick, new PlayerEntity(nick, world));
        }
        players.setController("MrBoomDev", ui.joystick);
    }
}