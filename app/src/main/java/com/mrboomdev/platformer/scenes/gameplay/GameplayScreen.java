package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.projectile.ProjectileCollision;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.LogUtil;

public class GameplayScreen extends CoreScreen {
	private final GameHolder game = GameHolder.getInstance();
	public static SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private ShaderProgram shaders;
	private Viewport viewport;
	private Sprite screenEffect;
	
	@Override
	public void render(float delta) {
		if(game.settings.pause) return;
		ScreenUtils.clear(0, 0, 0, 1);
		
		var camera = game.environment.camera;
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		batch.begin(); {
			//game.environment.render(batch);
		} batch.end();

		if(!game.settings.debugRaysDisable && game.environment.rayHandler != null) {
			//game.environment.rayHandler.setCombinedMatrix(camera);
			//game.environment.rayHandler.updateAndRender();
		}

		batch.begin(); {
			//game.environment.ui.draw(batch);
			//ui.render(delta);

			//screenEffect.draw(batch);
		} batch.end();

		game.environment.update(delta);
	}
	
	@Override
	public void show() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		screenEffect = new Sprite(new Texture("packs/official/src/effects/old_tv.png"));
		screenEffect.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		screenEffect.setAlpha(.5f);

		shaders = new ShaderProgram(
				Gdx.files.internal("world/shaders/effects/effects.vert"),
				Gdx.files.internal("world/shaders/effects/effects.frag"));

		ShaderProgram.pedantic = false;
		if(shaders.isCompiled()) {
			LogUtil.debug("Shaders", "Successfully compiled shaders!");
			batch.setShader(shaders);

			game.environment.batch = batch;
			game.environment.shader = shaders;
		} else {
			throw BoomException.builder("Failed to compile shaders!\nDefault shader logs: ")
					.addQuoted(shaders.getLog()).build();
		}
		
		game.environment.world.setContactListener(new ProjectileCollision());
		
		var camera = new OrthographicCamera(32, 18);
		camera.zoom = .75f;
		game.environment.camera = camera;
		viewport = new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera);

		//var path = game.settings.playerCharacter;
		/*game.environment.entities.loadCharacter(path, "klarrie");
		var player = new CharacterCreator(game.environment.entities.presets
				.get("klarrie")
				.cpy(game.settings.playerName, path))
				.create();*/
		
		/*game.settings.mainPlayer = player;
		game.environment.entities.setMain(player);
		camera.position.set(player.getPosition(), 0);*/

		//CameraUtil.setTarget(player);
		//AudioUtil.setTarget(player);
		
		//ui = new GameplayUi(this, player);
		//Gdx.input.setInputProcessor(ui.stage);
		if(game.settings.enableEditor) {
			//var editor = new EditorManager();
			//ui.attachLayerDrawer(editor);
		}

		//game.environment.start(ui.stage);
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
		if(game.environment.rayHandler != null) game.environment.rayHandler.dispose();
		//ui.dispose();
		shaders.dispose();
	}
}