package com.mrboomdev.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.mrboomdev.platformer.environment.MapManager;
import com.mrboomdev.platformer.manager.PlayersManager;
// import com.mrboomdev.platformer.render.MapRender;
import com.mrboomdev.platformer.ui.GameplayUi;
import com.mrboomdev.platformer.ui.TouchControls;

public class GameplayScene extends ApplicationAdapter implements Screen {
  private OrthographicCamera camera;
  private SpriteBatch sprites;
  private Music music;
  private MapManager map;
  private PlayersManager players;
  private TouchControls controls;
  private int screenWidth, screenHeight;
  private BitmapFont font;
  private World world;
  private Box2DDebugRenderer debugRenderer;
  private Body body;
  private BodyDef bodyDef;
  private float U = 0;
  private Sprite mySprite;
  private GameplayUi ui;

  public GameplayScene(int width, int height) {
    screenWidth = width;
    screenHeight = height;
  }

  @Override
  public void render(float delta) {
    world.step(1 / 60f, 6, 2);
    ScreenUtils.clear(0, 0, 0, 1);
	ui.render();
    camera.update();
    sprites.setProjectionMatrix(camera.combined);
    sprites.begin();
	
	mySprite.setPosition(body.getPosition().x - mySprite.getWidth() / 2, body.getPosition().y - mySprite.getHeight() / 2);
	mySprite.draw(sprites);

    ui.render();
    map.render();
    players.render(sprites);
    controls.render();
    world.step(1 / 60f, 6, 2);
    font.draw(sprites, "Hello, World!", 100, 100, 250, 1, false);
    font.draw(sprites, getDebugValues(), 10, 10, 250, 0, true);
    debugRenderer.render(world, camera.combined);

    sprites.end();

    Vector2 myPower = controls.joystick.getPower();
    Vector2 mySpeed = new Vector2(0, 0);
    if (myPower.x == 0 && myPower.y == 0) {
      U = 0;
    } else {
      U += 3;
      mySpeed = new Vector2(myPower.x > 0 ? U : -U, myPower.y > 0 ? U : -U);
    }
    camera.position.set(body.getPosition().x, body.getPosition().y, 0);
    body.setLinearVelocity(mySpeed.x, mySpeed.y);
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
	ui.dispose();
  }

  @Override
  public void show() {
	  Box2D.init();
    debugRenderer = new Box2DDebugRenderer();
    world = new World(new Vector2(0, -50f), true);
    camera = new OrthographicCamera();
    camera.setToOrtho(false, screenWidth, screenHeight);
    sprites = new SpriteBatch();
    font = new BitmapFont();
    font.setColor(Color.WHITE);

    players = new PlayersManager(world);

    controls = new TouchControls(sprites, screenHeight);
    Gdx.input.setInputProcessor(controls);

    String myNick = "MrBoomDev";
    players.create(myNick);

    String[] nicks = {"Arslan", "Amir", "Artur", "Kapusta", "FreddyFazbear123", "CatOMan", "Danilka", "Shu-nya@zavr.top"};
    for (String nick : nicks) {
      players.create(nick);
    }

	Texture myTexture = new Texture(Gdx.files.internal("img/player/player.jpg"));
	mySprite = new Sprite(myTexture);
	mySprite.setSize(100, 100);
    bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.DynamicBody;
    bodyDef.position.set(500, 150);
    body = world.createBody(bodyDef);
    PolygonShape circle = new PolygonShape();
    circle.setAsBox(mySprite.getWidth() / 2, mySprite.getHeight() / 2);
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = circle;
    fixtureDef.density = 1f; //скорость
    fixtureDef.friction = 1f; // трение
    fixtureDef.restitution = 1f; //масса
    Fixture fixture = body.createFixture(fixtureDef);
    circle.dispose();

    BodyDef floor = new BodyDef();
    bodyDef.position.set(new Vector2(1f, 10f));
    Body floorBody = world.createBody(floor);
    PolygonShape floorBox = new PolygonShape();
    floorBox.setAsBox(camera.viewportWidth, 10.0f);
    floorBody.createFixture(floorBox, 0.0f);
    floorBox.dispose();

    map = new MapManager(sprites);
	
	ui = new GameplayUi();
  }

  @Override
  public void hide() {}
}
