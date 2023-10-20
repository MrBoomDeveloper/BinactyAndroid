package com.mrboomdev.binacty.script.__legacy;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.mrboomdev.binacty.api.client.BinactyClient;
import com.mrboomdev.binacty.api.frontend.graphics.DrawableGraphic;
import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.character.CharacterCreator;
import com.mrboomdev.platformer.environment.editor.EditorManager;
import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.gameplay.GameplayScreen;
import com.mrboomdev.platformer.scenes.gameplay.GameplayUi;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.Objects;

public class LegacyFnafBridge {
	public MyScreen screen;
	public boolean isReady;
	private final Main main = new Main(this);

	public LegacyFnafBridge(@NonNull BinactyClient client) {
		screen = new MyScreen();
		screen.create();
		client.setScreen(screen);
	}

	private class MyScreen implements DrawableGraphic {
		private final GameHolder game = GameHolder.getInstance();
		private GameplayUi ui;
		private Box2DDebugRenderer debugRenderer;

		@Override
		public void create() {
			debugRenderer = new Box2DDebugRenderer();

			new Thread(() -> {
				try {
					var mapFile = BoomFile.internal("packs/fnaf/maps/fnafMap1.json");

					var moshi = new Moshi.Builder().add(new MapTile.Adapter()).build();
					var adapter = moshi.adapter(MapManager.class);
					var map = Objects.requireNonNull(adapter.fromJson(mapFile.readString()));

					game.environment.map = map;

					map.build(game.environment.world, mapFile.toFileUtil(), () -> Gdx.app.postRunnable(() -> {
						var path = game.settings.playerCharacter;

						game.environment.entities = new EntityManager();
						game.environment.entities.loadCharacter(path, "klarrie");

						var player = new CharacterCreator(game.environment.entities.presets
								.get("klarrie")
								.cpy(game.settings.playerName, path))
								.create(map);

						game.settings.mainPlayer = player;

						game.environment.setupRayHandler();
						game.environment.entities.setMain(player);

						CameraUtil.setCameraPosition(player.getPosition().x, player.getPosition().y);
						CameraUtil.setTarget(player);
						AudioUtil.setTarget(player);

						ui = new GameplayUi();
						Gdx.input.setInputProcessor(ui.stage);

						if(game.settings.enableEditor) {
							var editor = new EditorManager();
							ui.attachLayerDrawer(editor);
						}

						game.environment.start(ui.stage);
						game.environment.ui.connectCharacter(player);

						main.start();
					}));
				} catch(IOException e) {
					e.printStackTrace();
				}
			}).start();
		}

		@Override
		public void destroy() {}

		@Override
		public void update() {
			var map = game.environment.map;
			if(map != null) map.ping();
		}

		@Override
		public void render() {
			var batch = GameplayScreen.batch;
			if(batch == null) return;

			batch.begin();

			var map = game.environment.map;
			if(map != null) map.render(batch);

			batch.end();

			if(!game.settings.debugRaysDisable && game.environment.rayHandler != null) {
				game.environment.rayHandler.setCombinedMatrix(game.environment.camera);
				game.environment.rayHandler.updateAndRender();
			}

			batch.begin();

			if(ui != null) ui.render(Gdx.graphics.getDeltaTime());

			batch.end();

			if(game.settings.debugRenderer) {
				debugRenderer.render(game.environment.world, game.environment.camera.combined);
			}
		}
	}
}