package com.mrboomdev.binacty;

import androidx.annotation.NonNull;

import com.mrboomdev.binacty.api.client.BinactyClient;
import com.mrboomdev.binacty.api.frontend.graphics.DrawableGraphic;
import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.character.CharacterCreator;
import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.gameplay.GameplayScreen;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.Objects;

public class LegacyFnafBridge {
	private final GameHolder game = GameHolder.getInstance();

	public LegacyFnafBridge(@NonNull BinactyClient client) {
		var myScreen = new MyScreen();
		client.setScreen(myScreen);

		new Thread(() -> {
			try {
				var mapFile = BoomFile.internal("packs/fnaf/maps/fnafMap1.json");

				var moshi = new Moshi.Builder().add(new MapTile.Adapter()).build();
				var adapter = moshi.adapter(MapManager.class);
				var map = Objects.requireNonNull(adapter.fromJson(mapFile.readString()));

				game.environment.map = map;
				map.build(game.environment.world, mapFile.toFileUtil(), () -> {
					System.out.println("Finished building a map!");

					var path = game.settings.playerCharacter;
					game.environment.entities = new EntityManager();
					game.environment.entities.loadCharacter(path, "klarrie");

					var player = new CharacterCreator(game.environment.entities.presets
						.get("klarrie")
						.cpy(game.settings.playerName, path))
						.create();

					game.settings.mainPlayer = player;

					game.environment.setupRayHandler();
					game.environment.entities.setMain(player);

					player.setPosition(10, 10);

					CameraUtil.setCameraPosition(player.getPosition().x, player.getPosition().y);
					CameraUtil.setTarget(player);
					AudioUtil.setTarget(player);
				});
			} catch(IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private static class MyScreen implements DrawableGraphic {
		private final GameHolder game = GameHolder.getInstance();

		@Override
		public void create() {

		}

		@Override
		public void destroy() {

		}

		@Override
		public void render() {
			var batch = GameplayScreen.batch;
			if(batch == null) return;

			//batch.begin();

			var map = game.environment.map;
			if(map != null) {
				//map.ping();
				//map.render(batch);
			}

			//batch.end();
		}
	}
}