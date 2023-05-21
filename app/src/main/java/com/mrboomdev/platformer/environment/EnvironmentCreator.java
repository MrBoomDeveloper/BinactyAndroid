package com.mrboomdev.platformer.environment;

import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.BUILDING_MAP;
import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.DONE;
import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.LOADING_GAMEMODE_RESOURCES;
import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.PREPAIRING;

import com.badlogic.gdx.Gdx;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.environment.gamemode.GamemodeManager;
import com.mrboomdev.platformer.environment.gamemode.GamemodeScript;
import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class EnvironmentCreator {
	private final EnvironmentManager manager;
	private onCreateListener createListener;
	private final GameHolder game = GameHolder.getInstance();
	private Status status = PREPAIRING;
	
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
				Moshi moshi = new Moshi.Builder().build();
				JsonAdapter<MapManager> adapter = moshi.adapter(MapManager.class);
				manager.map = adapter.fromJson(game.mapFile.readString(true)).build(manager.world, game.mapFile, () -> loadGamemode());
				status = BUILDING_MAP;
			} catch(Exception e) {
				e.printStackTrace();
				Gdx.files.external("crash.txt").writeString("Crashed while starting building the map.\n" + e.getMessage(), false);
				game.launcher.exit(GameLauncher.Status.CRASH);
			}
		}).start();
		return this;
	}
	
	private void loadGamemode() {
		manager.entities = new EntityManager(manager.world);
		try {
			Moshi moshi = new Moshi.Builder().build();
			JsonAdapter<GamemodeScript> adapter = moshi.adapter(GamemodeScript.class);
			manager.gamemode = new GamemodeManager(adapter.fromJson(FileUtil.internal("packs/fnaf/gamemode.json").readString(true)), game.gamemodeFile)
				.build(() -> {
					createListener.created(manager);
					status = DONE;
				});
			this.status = LOADING_GAMEMODE_RESOURCES;
		} catch(Exception e) {
			e.printStackTrace();
			Gdx.files.external("crash.txt").writeString("Crashed while starting the gamemode.\n" + e.getMessage(), false);
			game.launcher.exit(GameLauncher.Status.CRASH);
		}
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
		PREPAIRING,
		BUILDING_MAP,
		LOADING_GAMEMODE_RESOURCES,
		DONE
	}
}