package com.mrboomdev.platformer.scenes.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.environment.EnvironmentCreator;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.scenes.gameplay.GameplayScreen;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles.File;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;

public class LoadingScreen extends CoreScreen {
    private MainGame game;
    private LoadScene loadScene;
    private AssetManager asset;
    private Sprite banner;
    private SpriteBatch batch;

    public enum LoadScene {
        LOBBY,
        GAMEPLAY
    }

    public LoadingScreen(LoadScene scene) {
        this.loadScene = scene;
        this.game = MainGame.getInstance();
        this.asset = game.asset;
        this.batch = new SpriteBatch();
        this.banner = new Sprite(asset.get("ui/banner/loading.jpg", Texture.class));
        banner.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        banner.setCenter(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    }

    @Override
    public void show() {
        switch(loadScene) {
            case LOBBY:
                MainGame.getInstance().toggleGameView(false);
                break;
            case GAMEPLAY:
                EnvironmentCreator creator = new EnvironmentCreator(world -> {
                    game.analytics.logInfo("WorldStatus", "Successfully created world!");
                }, exception -> {
                    game.analytics.logError("WorldStatus", "An exception has occured while creating a world.");
                    Gdx.app.exit();
                });
                creator.start();
                break;
        }
		
		Gson gson = new Gson();
		LoadingFiles files = gson.fromJson(Gdx.files.internal("etc/loadFiles.json").readString(), LoadingFiles.class);
		files.loadToManager(asset, loadScene.name());
    }

    @Override
    public void render(float delta) {
        if(asset.update(17)) {
            switch(loadScene) {
                case GAMEPLAY:
                    game.setScreen(new GameplayScreen());
                    break;
            }
        }
        
        batch.begin();
        banner.draw(batch);
        batch.end();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
    }
}