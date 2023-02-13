package com.mrboomdev.platformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.mrboomdev.platformer.NativeContainer;
import com.mrboomdev.platformer.scenes.splash.SplashScreen;
import com.mrboomdev.platformer.util.Analytics;

public class MainGame extends Game implements NativeContainer {
	public static final int SCREEN_INSET = 60;
	
	private static MainGame instance;
	private NativeContainer container;
	public Analytics analytics;
	public AssetManager asset;
	public String nick = "Player228";
	public int botsCount = 10;
	public boolean showBodyColissions;
	
	public static MainGame getInstance() {
		return instance;
	}
	
	public static MainGame getInstance(Analytics analytics, NativeContainer container) {
		instance = new MainGame(analytics, container);
		return instance;
	}
	
	private MainGame(Analytics analytics, NativeContainer container) {
		this.analytics = analytics;
		this.container = container;
		this.asset = new AssetManager() {
			@Override
			public synchronized void unload(String file) {
				analytics.logInfo("Assets", "Unload file: " + file);
				super.unload(file);
			}
			
			@Override
			public synchronized <T extends Object> void load(String file, Class<T> fileClass) {
				analytics.logDebug("Assets", "Load file: " + file);
				super.load(file, fileClass);
			}
		};
		
		FileHandleResolver resolver = new InternalFileHandleResolver();
		asset.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		asset.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
	}
	
	@Override
	public void create() {
		analytics.logDebug("Game", "MainGame.create()");
		setScreen(new SplashScreen());
	}
	
	@Override
	public void toggleGameView(boolean isActive) {
		analytics.logDebug("Game", "Toggle game view: " + (isActive ? "Active" : "Inactive"));
		container.toggleGameView(isActive);
	}
	
	@Override
	public void setScreen(Screen screen) {
		analytics.logDebug("Game", "Set screen: " + screen.getClass().getName());
		super.setScreen(screen);
	}
	
	@Override
	public void dispose() {
		analytics.logInfo("Game", "Dispose");
		super.dispose();
		asset.clear();
	}
}