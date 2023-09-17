package com.mrboomdev.platformer.game;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.mrboomdev.binacty.Constants;
import com.mrboomdev.platformer.environment.EnvironmentManager;
import com.mrboomdev.platformer.environment.logic.Trigger;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;
import com.mrboomdev.platformer.script.ScriptManager;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class GameHolder extends Game {
	public Bundle envVars = new Bundle();
	public GameLauncher launcher;
	public GameSettings settings;
	public GameAnalytics analytics;
	public AssetManager assets, externalAssets;
	public EnvironmentManager environment;
	public ScriptManager script;
	public GameStatistics stats;
	public FileUtil gamemodeFile, mapFile;
	private static GameHolder instance;

	static {
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> LogUtil.crash(throwable));
	}
	
	@Override
	public void create() {
		analytics.log("GameHolder", "create");
		
		try {
			var adapter = Constants.moshi.adapter(LoadingFiles.class);

			LoadingFiles files = adapter.fromJson(Gdx.files.internal("etc/loadFiles.json").readString());
			Objects.requireNonNull(files).loadToManager(assets, "LOADING");
		} catch(IOException e) {
			LogUtil.crash("Failed to load resources", "It looks, that the internal loader list was broken.", e);
			e.printStackTrace();
		}
		
		assets.finishLoading();
		setScreen(new LoadingScreen());
	}
	
	public static GameHolder setInstance(
			GameLauncher launcher,
			@NonNull GameSettings settings,
			@NonNull GameAnalytics analytics
	) {
		analytics.log("GameHolder", "setInstance");
		instance = new GameHolder(launcher, settings, analytics);
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
	
	private GameHolder(GameLauncher launcher, GameSettings settings, @NonNull GameAnalytics analytics) {
		analytics.log("GameHolder", "constructor");
		this.launcher = launcher;
		this.settings = settings;
		this.analytics = analytics;
		this.stats = new GameStatistics();
		this.assets = new Assets(analytics, new InternalFileHandleResolver());
		this.externalAssets = new Assets(analytics, new ExternalFileHandleResolver());
	}
	
	@Override
	public void dispose() {
		analytics.log("GameHolder", "dispose");
		assets.clear();
		externalAssets.clear();
		environment.world.dispose();
		super.dispose();
	}
	
	private static class Assets extends AssetManager {
		private final GameAnalytics analytics;
		
		public Assets(GameAnalytics analytics, FileHandleResolver resolver) {
			super(resolver);
			this.analytics = analytics;
			
			setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
			setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
			
			setErrorListener((asset, throwable) -> {
				analytics.error("Assets", "Failed to load asset: " + asset.fileName + ", of type: " + asset.type.getName());
				throw new BoomException(throwable);
			});
		}
		
		@Override
		public synchronized <T> void load(String file, Class<T> fileClass) {
			analytics.log("Assets", "Load file: " + file);
			super.load(file, fileClass);
		}
	}
}