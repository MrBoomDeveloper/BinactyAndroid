package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.mrboomdev.platformer.environment.EnvironmentManager;
import com.mrboomdev.platformer.util.FileUtil;

public class EnvironmentCreator {
	private EnvironmentManager manager;
	private onCreateListener createListener;
	private FileUtil gamemodeFile, mapFile;
	
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
	
	public void create() {
		new Thread(() -> {
			Gson gson = new Gson();
			manager.map = gson.fromJson(mapFile.readString(), EnvironmentMap.class)
				.build(manager.world, mapFile);
			createListener.created(manager);
		}).start();
	}
	
	public interface onCreateListener {
		void created(EnvironmentManager manager);
	}
}