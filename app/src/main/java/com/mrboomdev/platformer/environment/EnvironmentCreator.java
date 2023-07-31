package com.mrboomdev.platformer.environment;

import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.BUILDING_MAP;
import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.DONE;
import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.LOADING_GAMEMODE_RESOURCES;
import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.PREPARING;

import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.environment.gamemode.GamemodeManager;
import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.LogUtil;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class EnvironmentCreator {
	private final EnvironmentManager manager;
	private onCreateListener createListener;
	private final GameHolder game = GameHolder.getInstance();
	private Status status = PREPARING;
	
	public EnvironmentCreator() {
		this.manager = new EnvironmentManager();
	}
	
	public EnvironmentCreator onCreate(onCreateListener listener) {
		this.createListener = listener;
		return this;
	}
	
	public EnvironmentCreator create() {
		new Thread(() -> {
			try {
				Moshi moshi = new Moshi.Builder().add(new MapTile.Adapter()).build();
				JsonAdapter<MapManager> adapter = moshi.adapter(MapManager.class);
				manager.map = adapter.fromJson(game.mapFile.readString(true));
				if(manager.map == null) throw new BoomException("Null map file");
				manager.map.build(manager.world, game.mapFile, this::loadGamemode);
				status = BUILDING_MAP;
			} catch(Exception e) {
				LogUtil.crash("Failed to build a map", "It looks, that some files were corrupted.", e);
			}
		}).start();
		return this;
	}
	
	private void loadGamemode() {
		manager.entities = new EntityManager();

		manager.gamemode = new GamemodeManager(game.gamemodeFile)
				.build(() -> {
					createListener.created(manager);
					status = DONE;
				});

		this.status = LOADING_GAMEMODE_RESOURCES;
	}
	
	public String getStatus() {
		if(status == BUILDING_MAP) {
			manager.map.ping();
			return "Building the map...";
		}
		if(status == LOADING_GAMEMODE_RESOURCES) {
			manager.gamemode.ping();
			return "Loading gamemode resources...";
		}
		return status.name();
	}
	
	public interface onCreateListener {
		void created(EnvironmentManager manager);
	}
	
	public enum Status {
		PREPARING,
		BUILDING_MAP,
		LOADING_GAMEMODE_RESOURCES,
		DONE
	}
}