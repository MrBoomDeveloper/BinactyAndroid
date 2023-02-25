package com.mrboomdev.platformer.scenes.splash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;
import com.mrboomdev.platformer.util.Direction;
import com.mrboomdev.platformer.util.anime.AnimeManual;

public class SplashScreen extends CoreScreen {
	private SpriteBatch batch;
	private Sprite logo, gradient;
    private AssetManager asset;
    private AnimeManual anime = new AnimeManual();
    private float alphaProgress;
    private Direction direction = new Direction(Direction.FORWARD);
    
    @Override
    public void show() {
		Gson gson = new Gson();
        //asset = MainGame.getInstance().assets;
        LoadingFiles files = gson.fromJson(Gdx.files.internal("etc/loadFiles.json").readString(), LoadingFiles.class);
		files.loadToManager(asset, "LOADING");
	    batch = new SpriteBatch();
	  
	    logo = new Sprite(new Texture(Gdx.files.internal("ui/brand/dev_logo.png")));
	    logo.setScale(.4f);
	    logo.setCenter(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
	    gradient = new Sprite(new Texture(Gdx.files.internal("ui/brand/gradient.png")));
	    gradient.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        anime.addEntity(logo, gradient)
            .addTimecodeListener(1.8f, unused -> {
                direction.current = Direction.BACKWARD;
            })
            .addTimecodeListener(2.5f, unused -> {
                asset.finishLoading();
                //MainGame.getInstance().setScreen(new LoadingScreen(LoadingScreen.LoadScene.LOBBY));
            })
            .setUpdateListener((float delta, Array<Object> entries) -> {
                if(direction.isForward()) {
                    alphaProgress = Math.min(alphaProgress + (delta * 2), 1);
                } else if(direction.isBackward()) {
                    alphaProgress = Math.max(alphaProgress - (delta * 2), 0);
                }
                for(Object sprite : entries) {
                    ((Sprite)sprite).setAlpha(alphaProgress);
                }
            });
    }

    @Override
    public void render(float delta) {
	    Gdx.gl.glClearColor(17, 7, 31, alphaProgress);
  	  batch.begin();
		{
			logo.draw(batch);
	    	gradient.draw(batch);
		}
	    batch.end();
        anime.update(delta);
    }
  
    @Override
    public void dispose() {
	    batch.dispose();
    }
}