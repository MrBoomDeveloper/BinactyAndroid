package com.mrboomdev.platformer.scenes.loading;

import static com.mrboomdev.platformer.scenes.loading.LoadingScreen.LoadStep.MAP;
import static com.mrboomdev.platformer.scenes.loading.LoadingScreen.LoadStep.PREPARING;
import static com.mrboomdev.platformer.scenes.loading.LoadingScreen.LoadStep.RESOURCES;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mrboomdev.platformer.environment.EnvironmentCreator;
import com.mrboomdev.platformer.environment.EnvironmentManager;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.scenes.gameplay.GameplayScreen;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.LogUtil;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class LoadingScreen extends CoreScreen {
    private final GameHolder game = GameHolder.getInstance();
    private final Sprite banner;
    private final SpriteBatch batch;
	private final BitmapFont font;
	private EnvironmentCreator environmentCreator;
	private EnvironmentManager environment;
	private LoadStep loadStep = PREPARING;

    public LoadingScreen() {
        this.batch = new SpriteBatch();
		this.banner = new Sprite(game.assets.get("packs/official/src/images/banner.jpg", Texture.class));
		this.banner.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.banner.setCenter((float)Gdx.graphics.getWidth() / 2, (float)Gdx.graphics.getHeight() / 2);
		this.font = game.assets.get("loading.ttf", BitmapFont.class);
    }

    @Override
    public void show() {
		try {
			var moshi = new Moshi.Builder().build();
			var adapter = moshi.adapter(LoadingFiles.class);
			LoadingFiles files = adapter.fromJson(Gdx.files.internal("etc/loadFiles.json").readString());
			if(files == null) throw new BoomException("LoadingFiles cannot be null.");
			files.loadToManager(game.assets, "GAMEPLAY");
		} catch(IOException e) {
			e.printStackTrace();
		}

		try {
			environmentCreator = new EnvironmentCreator().onCreate(manager -> {
				this.environment = manager;
				loadOtherResources();
			}).create();

			loadStep = MAP;
		} catch(Exception e) {
			LogUtil.crash("Failed to create the environment", "", e);
			e.printStackTrace();
		}
    }
	
	private void loadOtherResources() {
		this.loadStep = RESOURCES;
	}

    @Override
    public void render(float delta) {
		batch.begin(); {
			banner.draw(batch);
			font.draw(batch, getStatus(), 50, 75);
		} batch.end();
		
		if(game.assets.update(17) && game.externalAssets.update(17) && loadStep == RESOURCES) {
			game.setScreen(new GameplayScreen(environment));
        }
    }
	
	private String getStatus() {
		switch(loadStep) {
			case MAP: return environmentCreator.getStatus();
			case RESOURCES: return "Loading resources...";
			default: return "Preparing...";
		}
	}
    
    @Override
    public void dispose() {
        batch.dispose();
    }

	
	public enum LoadStep {
		PREPARING,
		MAP,
		RESOURCES
	}
}