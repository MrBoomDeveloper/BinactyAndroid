package com.mrboomdev.platformer.game;

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
import com.mrboomdev.platformer.environment.EnvironmentManager;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;
import com.mrboomdev.platformer.script.ScriptManager;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Moshi;

import java.io.IOException;

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
		
		try {
			var moshi = new Moshi.Builder().build();
			var adapter = moshi.adapter(LoadingFiles.class);
			LoadingFiles files = adapter.fromJson(Gdx.files.internal("etc/loadFiles.json").readString());

			files.loadToManager(assets, "LOADING");
			files.loadToManager(assets, "LOBBY");
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		assets.finishLoading();
		setScreen(new LoadingScreen(LoadingScreen.LoadScene.GAMEPLAY));
	}
	
	public static GameHolder setInstance(GameLauncher launcher, GameSettings settings, @NonNull GameAnalytics analytics) {
		analytics.log("GameHolder", "setInstance");
		instance = new GameHolder(launcher, settings, analytics);
		return instance;
	}
	
	public static GameHolder getInstance() {
		if(instance == null) {
			throw new BoomException("You need to set the instance first!");
		}
		return instance;
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
	
	public GameHolder() {}
}