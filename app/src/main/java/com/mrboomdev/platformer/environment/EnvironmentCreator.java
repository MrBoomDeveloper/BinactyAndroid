package com.mrboomdev.platformer.environment;

import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.BUILDING_MAP;
import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.DONE;
import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.LOADING_GAMEMODE_RESOURCES;
import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.LOADING_REQUIRED;
import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.PREPARING;

import com.mrboomdev.binacty.Constants;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.environment.gamemode.GamemodeManager;
import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import com.mrboomdev.platformer.script.ScriptManager;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.LogUtil;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class EnvironmentCreator {
	private final EnvironmentManager manager;
	private final GameHolder game = GameHolder.getInstance();
	private Status status = PREPARING;
	private Runnable loadRequiredCallback;
	
	public EnvironmentCreator() {
		manager = new EnvironmentManager();
		game.environment = manager;
	}

	public void loadRequiredResources(Runnable callback) throws IOException {
		if(status == LOADING_REQUIRED) return;
		this.status = LOADING_REQUIRED;
		this.loadRequiredCallback = callback;

		var adapter = Constants.moshi.adapter(LoadingFiles.class);
		var path = FileUtil.internal("etc/loadFiles.json");
		var files = adapter.fromJson(path.readString(true));

		if(files == null) throw new BoomException("LoadingFiles cannot be null.");

		files.loadToManager(game.assets, "GAMEPLAY");
	}

	public void loadMap() throws IOException {
		if(status == BUILDING_MAP) return;
		this.status = BUILDING_MAP;

		var moshi = new Moshi.Builder().add(new MapTile.Adapter()).build();
		var adapter = moshi.adapter(MapManager.class);
		manager.map = adapter.fromJson(game.mapFile.readString(true));

		if(manager.map == null) {
			throw new BoomException("Null map file");
		}

		manager.map.build(manager.world, game.mapFile, this::loadGamemode);
	}

	public void ping() {
		if(game.assets.update() && game.externalAssets.update()) {
			if(status == LOADING_REQUIRED) {
				loadRequiredCallback.run();
				/*try {
					loadMap();
				} catch(IOException e) {
					throw new BoomException("Failed to build a map", e);
				}*/
			}
		}
	}

	public void start() {
		try {
			loadRequiredResources(() -> {

			});
		} catch(IOException e) {
			throw new BoomException("Failed to load required resources", e);
		}
	}

	public void loadScripts() {
		game.script = new ScriptManager(null, null, null);
	}
	
	private void loadGamemode() {
		if(status == LOADING_GAMEMODE_RESOURCES) return;
		status = LOADING_GAMEMODE_RESOURCES;

		LogUtil.debug("GameStart", "Start the gamemode");

		manager.entities = new EntityManager();
		manager.gamemode = new GamemodeManager(game.gamemodeFile).build(() -> status = DONE);
	}
	
	public String getStatus() {
		switch(status) {
			case LOADING_GAMEMODE_RESOURCES: return manager.gamemode.ping();

			case LOADING_REQUIRED: {
				int progress = Math.round((game.assets.getProgress() + game.externalAssets.getProgress()) * 50);
				return "Loading required resources " + progress + "%";
			}

			case BUILDING_MAP: {
				if(manager.map == null) {
					return "Map is being created...";
				}

				return manager.map.ping();
			}

			case PREPARING: return "Preparing...";
			case DONE: return "Done!";
			default: return "Loading...";
		}
	}

	public Status getBareStatus() {
		return status;
	}
	
	public enum Status {
		PREPARING,
		LOADING_REQUIRED,
		BUILDING_MAP,
		LOADING_GAMEMODE_RESOURCES,
		DONE
	}
}