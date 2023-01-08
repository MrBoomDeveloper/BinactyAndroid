package com.mrboomdev.platformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.mrboomdev.platformer.scenes.splash.SplashScreen;
import com.mrboomdev.platformer.util.Analytics;

public class MainGame extends Game {
  private static MainGame instance;
  private Analytics analytics;
  public AssetManager asset;

  public static MainGame getInstance() {
    return instance;
  }

  public static MainGame getInstance(Analytics analytics) {
    if (instance == null) {
      instance = new MainGame(analytics);
    }
    return instance;
  }

  private MainGame(Analytics analytics) {
    this.analytics = analytics;
    this.asset = new AssetManager() {
      @Override
      public synchronized void unload(String file) {
        analytics.logInfo("UnloadFile", file);
        super.unload(file);
      }

      @Override
      public synchronized <T extends java.lang.Object> void load(String file, Class<T> fileClass) {
        analytics.logInfo("LoadFile", file + " : " + fileClass.getName());
        super.load(file, fileClass);
      }
    };
  }

  @Override
  public void create() {
    analytics.logDebug("Start game", "MainGame.create()");
    setScreen(new SplashScreen());
  }

  @Override
  public void setScreen(Screen screen) {
    analytics.logDebug("setScreen", screen.getClass().getName());
    super.setScreen(screen);
  }

  @Override
  public void dispose() {
    super.dispose();
    asset.clear();
  }
}
