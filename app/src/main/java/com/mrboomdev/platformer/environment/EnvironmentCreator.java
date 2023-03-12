package com.mrboomdev.platformer.environment;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.mrboomdev.platformer.environment.EnvironmentManager;
import com.mrboomdev.platformer.environment.gamemode.GamemodeManager;
import com.mrboomdev.platformer.environment.gamemode.GamemodeScript;
import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.*;
import com.mrboomdev.platformer.environment.map.MapManager;
import com.mrboomdev.platformer.util.FileUtil;

public class EnvironmentCreator {
	private EnvironmentManager manager;
	private onCreateListener createListener;
	private FileUtil gamemodeFile, mapFile;
	private Status status = PREPAIRING;
	
	public EnvironmentCreator() {
		this.manager = new EnvironmentManager();
	}
	
	public EnvironmentCreator setGamemode(FileUtil file) {
		this.gamemodeFile = file;
		return this;
	}
	
	public EnvironmentCreator setMap(FileUtil file) {
		this.mapFile = file;
		return this;
	}
	
	public EnvironmentCreator onCreate(onCreateListener listener) {
		this.createListener = listener;
		return this;
	}
	
	public EnvironmentCreator create() {
		Gson gson = new Gson();
		new Thread(() -> {
			manager.map = gson.fromJson(mapFile.readString(true), MapManager.class)
				.build(manager.world, mapFile, () -> loadGamemode());
			status = BUILDING_MAP;
		}).start();
		return this;
	}
	
	private void loadGamemode() {
		Gson gson = new Gson();
		manager.gamemode = new GamemodeManager(
			gson.fromJson(gamemodeFile.readString(true), GamemodeScript.class)
		).build(gamemodeFile, () -> {
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
		PREPAIRING,
		BUILDING_MAP,
		LOADING_GAMEMODE_RESOURCES,
		DONE
	}
}