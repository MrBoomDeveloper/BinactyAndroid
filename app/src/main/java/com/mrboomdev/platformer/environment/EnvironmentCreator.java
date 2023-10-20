package com.mrboomdev.platformer.environment;

import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.LOADING_REQUIRED;
import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.LOADING_SCRIPTS;
import static com.mrboomdev.platformer.environment.EnvironmentCreator.Status.PREPARING;

import com.mrboomdev.binacty.Constants;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;

import java.io.IOException;

public class EnvironmentCreator {
	private final GameHolder game = GameHolder.getInstance();
	private Status status = PREPARING;
	private Runnable loadRequiredCallback;
	private boolean didLoadedRequired;
	
	public EnvironmentCreator() {
		game.environment = new EnvironmentManager();
	}

	public void loadRequiredResources(Runnable callback) throws IOException {
		if(status == LOADING_REQUIRED) return;
		this.status = LOADING_REQUIRED;
		this.loadRequiredCallback = callback;

		var adapter = Constants.moshi.adapter(LoadingFiles.class);
		var path = FileUtil.internal("etc/loadFiles.json");
		var files = adapter.fromJson(path.readString());

		if(files == null) throw new BoomException("LoadingFiles cannot be null.");

		files.loadToManager(game.assets, "GAMEPLAY");
	}

	public void ping() {
		if(game.assets.update() && game.externalAssets.update()) {
			if(status == LOADING_REQUIRED && !didLoadedRequired) {
				loadRequiredCallback.run();
				didLoadedRequired = true;
			}
		}
	}

	public void start() {
		try {
			loadRequiredResources(this::loadScripts);
		} catch(IOException e) {
			throw new BoomException("Failed to load required resources", e);
		}
	}

	public void loadScripts() {
		if(status == LOADING_SCRIPTS) return;
		status = LOADING_SCRIPTS;

		game.script.compile(game.entries);
	}
	
	public String getStatus() {
		switch(status) {
			case LOADING_SCRIPTS: return game.script.ping();

			case LOADING_REQUIRED: {
				int progress = Math.round((game.assets.getProgress() + game.externalAssets.getProgress()) * 50);
				return "Loading required resources " + progress + "%";
			}

			case PREPARING: return "Preparing...";
			default: return "Loading...";
		}
	}
	
	public enum Status {
		PREPARING,
		LOADING_SCRIPTS,
		LOADING_REQUIRED
	}
}