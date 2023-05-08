package com.mrboomdev.platformer.scenes.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mrboomdev.platformer.environment.EnvironmentCreator;
import com.mrboomdev.platformer.environment.EnvironmentManager;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.scenes.gameplay.GameplayScreen;
import static com.mrboomdev.platformer.scenes.loading.LoadingScreen.LoadStep.*;
import com.squareup.moshi.Moshi;
import java.io.IOException;

public class LoadingScreen extends CoreScreen {
    private GameHolder game = GameHolder.getInstance();
    private LoadScene loadScene;
    private Sprite banner;
    private SpriteBatch batch;
	private BitmapFont font;
	private EnvironmentCreator environmentCreator;
	private EnvironmentManager environment;
	private LoadStep loadStep = PREPAIRING;

    public LoadingScreen(LoadScene scene) {
        this.loadScene = scene;
        this.batch = new SpriteBatch();
        this.banner = new Sprite(game.assets.get("packs/fnaf/src/banner.png", Texture.class));
        this.banner.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.banner.setCenter(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		this.font = game.assets.get("loading.ttf", BitmapFont.class);
    }

    @Override
    public void show() {
		try {
			var moshi = new Moshi.Builder().build();
			var adapter = moshi.adapter(LoadingFiles.class);
			LoadingFiles files = adapter.fromJson(Gdx.files.internal("etc/loadFiles.json").readString());
			files.loadToManager(game.assets, loadScene.name());
		} catch(IOException e) {
			e.printStackTrace();
		}
		
        switch(loadScene) {
            case LOBBY:
				loadOtherResources();
                game.launcher.exit(GameLauncher.Status.LOBBY);
                break;
			
            case GAMEPLAY:
				try {
					environmentCreator = new EnvironmentCreator().onCreate(manager -> {
						this.environment = manager;
						loadOtherResources();
					}).create();
					loadStep = MAP;
				} catch(Exception e) {
					game.launcher.exit(GameLauncher.Status.CRASH);
					e.printStackTrace();
				}
                break;
        }
    }
	
	private void loadOtherResources() {
		this.loadStep = RESOURCES;
	}

    @Override
    public void render(float delta) {
        batch.begin();
		{
			banner.draw(batch);
			font.draw(batch, getStatus(), 50, 75);
		}
        batch.end();
		
		if(game.assets.update(17) && game.externalAssets.update(17) && loadStep == RESOURCES) {
            switch(loadScene) {
                case GAMEPLAY:
                    game.setScreen(new GameplayScreen(environment));
                    break;
            }
        }
    }
	
	private String getStatus() {
		switch(loadStep) {
			case MAP:
				return environmentCreator.getStatus();
			case RESOURCES:
				return "Loading resources...";
			default:
				return "Prepairing...";
		}
	}
    
    @Override
    public void dispose() {
        batch.dispose();
    }
	
	public enum LoadScene {
        LOBBY,
        GAMEPLAY
    }
	
	public enum LoadStep {
		PREPAIRING,
		MAP,
		RESOURCES
	}
}