package com.mrboomdev.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Collections;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mrboomdev.platformer.manager.PlayersManager;
import com.mrboomdev.platformer.render.MapRender;
import com.mrboomdev.platformer.ui.TouchControls;

public class GameplayScene extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch sprites;
    private Music music;
    private MapRender map;
    private PlayersManager players;
    private TouchControls controls;
    private int screenWidth, screenHeight;
    private BitmapFont font;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Body body;
    
    public GameplayScene(int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }
    
    @Override
    public void create() {
        /*music = Gdx.audio.newMusic(Gdx.files.internal("music/test.mp3"));
        music.setLooping(true);
        music.play();*/
        
        Box2D.init();
        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0, 0), true);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);
        sprites = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        
        map = new MapRender(sprites);
        //players = new PlayerRender(sprites, world);
        players = new PlayersManager(world);
        
        controls = new TouchControls(sprites, screenHeight);
        Gdx.input.setInputProcessor(controls);
        
        String myNick = "MrBoomDev";
        players.create(myNick);
        
        String[] nicks = {"Arslan", "Amir", "Artur", "Kapusta", "FreddyFazbear123"};
        for(String nick : nicks) {
            players.create(nick);
        }
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(500, 150);
        body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(6f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        //fixtureDef.density = 1f; //скорость
        fixtureDef.friction = .1f; //трение
        //fixtureDef.restitution = 100f; //масса
        Fixture fixture = body.createFixture(fixtureDef);
        circle.dispose();
        
        BodyDef floor = new BodyDef();
        bodyDef.position.set(new Vector2(1f, 10f));
        Body floorBody = world.createBody(floor);
        PolygonShape floorBox = new PolygonShape();
        floorBox.setAsBox(camera.viewportWidth, 10.0f);
        floorBody.createFixture(floorBox, 0.0f);
        floorBox.dispose();
    }
    
    @Override
    public void render() {
        world.step(1/60f, 6, 2);
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        sprites.setProjectionMatrix(camera.combined);
        sprites.begin();
        
        //map.render();
        players.render(sprites);
        controls.render();world.step(1/60f, 6, 2);
        font.draw(sprites, getDebugValues(), 10, 10, 250, 0, true);
        debugRenderer.render(world, camera.combined);
        
        sprites.end();
        
        //body.applyLinearImpulse(10f, 10f, 1f, 1f, true);
        
		if(controls.joystick.isActive) {
			body.applyLinearImpulse(controls.joystick.getPower(), body.getPosition(), true);
		}
        
        //players.moveBy("MrBoomDev", (int)controls.joystick.powerX, (int)controls.joystick.powerY);
    }
    
    public String getDebugValues() {
        String result = "FPS: " + Gdx.graphics.getFramesPerSecond();
        result += "\n" + controls.joystick.getDebugValues();
        return result;
    }
    
    @Override
    public void dispose() {
        controls.joystick.dispose();
        font.dispose();
    }
}


