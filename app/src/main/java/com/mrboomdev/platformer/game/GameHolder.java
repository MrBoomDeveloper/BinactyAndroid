package com.mrboomdev.platformer.game;

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
import com.google.gson.Gson;
import com.mrboomdev.platformer.environment.EnvironmentManager;
import com.mrboomdev.platformer.game.GameStatistics;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;
import com.mrboomdev.platformer.script.ScriptManager;
import com.mrboomdev.platformer.util.io.FileUtil;

public class GameHolder extends Game {
	public GameLauncher launcher;
	public GameSettings settings;
	public GameAnalytics analytics;
	public AssetManager assets, externalAssets;
	public EnvironmentManager environment;
	public ScriptManager script;
	public GameStatistics stats;
	public FileUtil gamemodeFile, mapFile;
	private static GameHolder instance;
	
	@Override
	public void create() {
		analytics.log("GameHolder", "create");
		
		Gson gson = new Gson();
		LoadingFiles files = gson.fromJson(Gdx.files.internal("etc/loadFiles.json").readString(), LoadingFiles.class);
		files.loadToManager(assets, "LOADING");
		files.loadToManager(assets, "LOBBY");
		assets.finishLoading();
		
		setScreen(new LoadingScreen(LoadingScreen.LoadScene.GAMEPLAY));
	}
	
	public static GameHolder setInstance(GameLauncher launcher, GameSettings settings, GameAnalytics analytics) {
		analytics.log("GameHolder", "setInstance");
		instance = new GameHolder(launcher, settings, analytics);
		return instance;
	}
	
	public static GameHolder getInstance() {
		if(instance == null) {
			throw new RuntimeException("You need to set the instance first!");
		}
		return instance;
	}
	
	private GameHolder(GameLauncher launcher, GameSettings settings, GameAnalytics analytics) {
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
	
	private class Assets extends AssetManager {
		private GameAnalytics analytics;
		
		public Assets(GameAnalytics analytics, FileHandleResolver resolver) {
			super(resolver);
			this.analytics = analytics;
			
			setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
			setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
			
			setErrorListener((asset, throwable) -> {
				analytics.error("Assets", "Failed to load asset: " + asset.fileName + ", of type: " + asset.type.getName());
				throw new RuntimeException(throwable);
			});
		}
		
		@Override
		public synchronized <T extends Object> void load(String file, Class<T> fileClass) {
			analytics.log("Assets", "Load file: " + file);
			super.load(file, fileClass);
		}
	}
}