package com.mrboomdev.platformer.game;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.mrboomdev.binacty.Constants;
import com.mrboomdev.binacty.game.core.CoreLauncher;
import com.mrboomdev.binacty.script.ScriptManager;
import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.platformer.environment.EnvironmentManager;
import com.mrboomdev.platformer.environment.logic.Trigger;
import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.scenes.gameplay.GameplayScreen;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameHolder extends Game {
	public List<PackData.GamemodeEntry> entries;
	public PackData.LevelsCategory.Level level;
	public Bundle envVars = new Bundle();
	public CoreLauncher launcher;
	public GameSettings settings;
	public AssetManager assets, externalAssets;
	public EnvironmentManager environment;
	public ScriptManager script;
	public GameStatistics stats;
	public Map<String, Screen> screens;
	private boolean wasReady;
	private static final String TAG = "GameHolder";
	private static GameHolder instance;

	static {
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			LogUtil.crash(throwable);
		});
	}

	public boolean isReady() {
		if(wasReady) return true;
		if(entries == null || entries.isEmpty()) return false;

		return (wasReady = true);
	}

	@Override
	public void render() {
		super.render();

		for(var entry : script.getEntries()) {
			var client = entry.getClient();

			if(client != null) {
				if(client.isReady()) client.render();
				client.update();
			}
		}

		checkIfNeedToChangeScreen();
	}

	private void checkIfNeedToChangeScreen() {
		if(script.isReady()) {
			if(getScreen() instanceof LoadingScreen) setScreen(getScreen("gameplay"));
		} else {
			if(getScreen() instanceof GameplayScreen) setScreen(getScreen("loading"));
		}
	}

	private Screen getScreen(String name) {
		Screen screen = null;

		if(screens.containsKey(name)) {
			screen = screens.get(name);
		} else {
			if(name.equals("loading")) screen = new LoadingScreen();
			if(name.equals("gameplay")) screen = new GameplayScreen();

			screens.put(name, screen);
		}

		return screen;
	}

	@Override
	public void create() {
		LogUtil.debug(TAG, "create");
		
		try {
			var adapter = Constants.moshi.adapter(LoadingFiles.class);
			var fileJson = BoomFile.internal("etc/loadFiles.json").readString();

			var files = Objects.requireNonNull(adapter.fromJson(fileJson));
			files.loadToManager(assets, "LOADING");

			assets.finishLoading();
		} catch(IOException e) {
			throw new BoomException("Failed to load required resources!", e);
		}

		screens = new HashMap<>();
		setScreen(getScreen("loading"));
	}
	
	public static GameHolder setInstance(
			CoreLauncher launcher,
			@NonNull GameSettings settings,
			List<PackData.GamemodeEntry> entries
	) {
		LogUtil.debug(TAG, "setInstance");

		instance = new GameHolder(launcher, settings, entries);
		instance.reset();

		return instance;
	}
	
	public static GameHolder getInstance() {
		if(instance == null) {
			throw new BoomException("You need to set the instance first!");
		}

		return instance;
	}

	public void reset() {
		CameraUtil.reset();
		Trigger.triggers = new ArrayList<>();

		if(settings.enableEditor) {
			settings.isUiVisible = true;
			settings.isControlsEnabled = true;
		}
	}
	
	private GameHolder(CoreLauncher launcher, GameSettings settings, List<PackData.GamemodeEntry> entries) {
		this.launcher = launcher;
		this.settings = settings;
		this.entries = entries;

		this.stats = new GameStatistics();
		this.script = new ScriptManager();

		this.assets = new Assets(new InternalFileHandleResolver());
		this.externalAssets = new Assets(new ExternalFileHandleResolver());
	}
	
	@Override
	public void dispose() {
		LogUtil.debug(TAG, "dispose");

		assets.clear();
		externalAssets.clear();
		environment.world.dispose();

		super.dispose();
	}
	
	private static class Assets extends AssetManager {
		private static final String TAG = "GameHolderAssets";
		
		public Assets(FileHandleResolver resolver) {
			super(resolver);
			
			setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
			setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
			
			setErrorListener((asset, throwable) -> {
				LogUtil.error(TAG, "Failed to load asset: " + asset.fileName + ", of type: " + asset.type.getName());
				throw new BoomException(throwable);
			});
		}
		
		@Override
		public synchronized <T> void load(String file, @NonNull Class<T> fileClass) {
			LogUtil.debug(TAG, "Load file: \"" + file + "\", as: " + fileClass.getSimpleName());
			super.load(file, fileClass);
		}
	}
}