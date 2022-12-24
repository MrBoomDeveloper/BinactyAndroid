package com.mrboomdev.platformer.scenes.splash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.scenes.gameplay.GameplayScreen;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.util.anime.AnimeStep;
import com.mrboomdev.platformer.util.anime.AnimeUtil;

public class SplashScreen extends CoreScreen {
	private MainGame game;
	private SpriteBatch batch;
	private Sprite logo, gradient;
	private float progress = 0;
	private final float LOGO_STAY_DURATION = 1.8f;
	
	public SplashScreen(MainGame game) {
		this.game = game;
	}

  @Override
  public void show() {
	  batch = new SpriteBatch();
	  
	  logo = new Sprite(new Texture(Gdx.files.internal("ui/brand/dev_logo.png")));
	  logo.setScale(.4f);
	  logo.setCenter(
	  	Gdx.graphics.getWidth() / 2, 
		  Gdx.graphics.getHeight() / 2
	  );
      
	  gradient = new Sprite(new Texture(Gdx.files.internal("ui/brand/gradient.png")));
	  gradient.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      
      new AnimeUtil()
          .addStep(new AnimeStep(AnimeStep.Type.FADE, 0, 1, 1000), 0)
          .addStep(new AnimeStep(AnimeStep.Type.FADE, 1, 0, 750), 2000)
          .runAsync(logo, gradient);
          
      new AnimeUtil()
          .addStep(new AnimeStep(AnimeStep.Type.SCALE, .4f, .6f, 2000), 0)
          .runAsync(logo);
  }

  @Override
  public void render(float delta) {
	  Gdx.gl.glClearColor(17, 7, 31, progress);
	  batch.begin();
	  
	  progress += delta * 1.8f;
	  if(progress < 1) {
		  logo.setAlpha(progress);
		  gradient.setAlpha(progress);
	  } else if(progress > LOGO_STAY_DURATION + 1) {
		  float screenAlpha = (progress < LOGO_STAY_DURATION + 2) ? (LOGO_STAY_DURATION + 2 - progress) : 0;
		  logo.setAlpha(screenAlpha);
		  gradient.setAlpha(screenAlpha);
	  } else {
		  logo.setAlpha(1);
		  gradient.setAlpha(1);
	  }
	  
	  logo.draw(batch);
	  gradient.draw(batch);
	  
	  batch.end();
	  
	  if(progress > (LOGO_STAY_DURATION + 2.3)) {
		  game.setScreen(new GameplayScreen());
	  }
  }
  
  @Override
  public void dispose() {
	  batch.dispose();
  }
}