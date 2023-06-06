package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mrboomdev.platformer.entity.character.CharacterCreator;
import com.mrboomdev.platformer.environment.EnvironmentManager;
import com.mrboomdev.platformer.environment.editor.EditorManager;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.projectile.ProjectileCollision;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.util.FunUtil;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;

import box2dLight.RayHandler;

public class GameplayScreen extends CoreScreen {
	public EnvironmentManager environment;
	private final GameHolder game;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private GameplayUi ui;
	private RayHandler rayHandler;
	private Box2DDebugRenderer debugRenderer;
	private ShaderProgram shaders;
	private Viewport viewport;

	public GameplayScreen(EnvironmentManager manager) {
		this.environment = manager;
		this.game = GameHolder.getInstance();
	}
	
	@Override
	public void render(float delta) {
		if(game.settings.pause) return;
		ScreenUtils.clear(0, 0, 0, 1);
		
		var camera = game.environment.camera;
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		batch.begin(); {
			environment.render(batch);
		} batch.end();

		if(!game.settings.debugRaysDisable) {
			rayHandler.setCombinedMatrix(camera);
			rayHandler.updateAndRender();
		}

		batch.begin(); {
			if(game.settings.debugRenderer) debugRenderer.render(environment.world, camera.combined);
			environment.ui.draw(batch);
			ui.render(delta);
		} batch.end();

		environment.update(delta);
		FunUtil.update();
		AudioUtil.Companion.update();
	}
	
	@Override
	public void show() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		debugRenderer = new Box2DDebugRenderer();
		
		shaders = new ShaderProgram(Gdx.files.internal("world/shaders/default/default.vert"), Gdx.files.internal("world/shaders/default/default.frag"));
		ShaderProgram.pedantic = false;
		if(shaders.isCompiled()) {
			game.analytics.log("Shaders", "Successfully compiled shaders!");
			batch.setShader(shaders);
		} else {
			game.analytics.error("Shaders", "Failed to compile shaders!");
			game.analytics.error("Shaders", shaders.getLog());
			game.launcher.exit(GameLauncher.Status.CRASH);
		}
		
		environment.world.setContactListener(new ProjectileCollision());
		environment.setupRayHandler();
		rayHandler = environment.rayHandler;
		
		var camera = new OrthographicCamera(32, 18);
		camera.zoom = .75f;
		environment.camera = camera;
		viewport = new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera);
		
		var path = FileUtil.internal("packs/official/src/characters/klarrie");
		environment.entities.loadCharacter(path, "klarrie");
		var player = new CharacterCreator(environment.entities.presets.get("klarrie").cpy(game.settings.playerName, path)).create();
		
		game.settings.mainPlayer = player;
		game.environment.entities.setMain(player);
		camera.position.set(player.getPosition(), 0);
		
		ui = new GameplayUi(this, player);
		Gdx.input.setInputProcessor(ui.stage);
		if(game.settings.enableEditor) {
			EditorManager editor = new EditorManager();
			ui.attachLayerDrawer(editor);
		}
		environment.start(ui.stage);
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
		rayHandler.dispose();
		ui.dispose();
		shaders.dispose();
	}
}