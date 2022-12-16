package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.mrboomdev.platformer.scenes.gameplay.GameplayUi;

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
        world.step(1 / 60f, 6, 2);
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
	
        map.render(batch, MapLayer.BACKGROUND);
        players.render(batch);
        map.render(batch, MapLayer.FOREGROUND);
    	ui.render();
        debugRenderer.render(world, camera.combined);

        batch.end();
        camera.position.set(players.getPosition("MrBoomDev"), 0);
    }

    @Override
    public void dispose() {
	    ui.dispose();
    }

    @Override
    public void show() {
	    Box2D.init();
        debugRenderer = new Box2DDebugRenderer();
        
        world = new World(new Vector2(0, -1), true);
        camera = new OrthographicCamera(75, 35);
        batch = new SpriteBatch();
    
        ui = new GameplayUi();
        Gdx.input.setInputProcessor(ui.stage);

        players = new PlayersManager(world);
        String[] nicks = {"MrBoomDev", "Kapusta", "FreddyFazbear123", "CatOMan", "Amogus"};
        for(String nick : nicks) {
            players.add(nick, new PlayerEntity(nick, world));
        }
        players.setController("MrBoomDev", ui.joystick);

        map = new MapManager(batch);
        BodyDef floor = new BodyDef();
        floor.position.set(new Vector2(0, -5f));
        Body floorBody = world.createBody(floor);
        PolygonShape floorBox = new PolygonShape();
        floorBox.setAsBox(camera.viewportWidth, 2f);
        floorBody.createFixture(floorBox, 0.0f);
        floorBox.dispose();
    }
}