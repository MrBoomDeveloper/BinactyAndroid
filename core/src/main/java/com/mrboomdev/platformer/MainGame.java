package com.mrboomdev.platformer;

import com.badlogic.gdx.Game;
import com.mrboomdev.platformer.ui.screens.SplashScreen;

public class MainGame extends Game {

  @Override
  public void create() {
	  setScreen(new SplashScreen(this));
  }

  @Override
  public void render() {
	  super.render();
  }

  @Override
  public void pause() {
	  super.pause();
  }

  @Override
  public void resume() {
	  super.resume();
  }

  @Override
  public void dispose() {
	  super.dispose();
  }
}