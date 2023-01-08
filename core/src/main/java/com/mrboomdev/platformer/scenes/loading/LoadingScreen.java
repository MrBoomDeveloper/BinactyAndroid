package com.mrboomdev.platformer.scenes.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.scenes.gameplay.GameplayScreen;
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
                //TODO
                break;
            case GAMEPLAY:
                asset.load("ui/overlay/big_elements.png", Texture.class);
                asset.load("audio/music/lobby_theme.mp3", Music.class);
                asset.load("effects/boom.png", Texture.class);
                asset.load("audio/sounds/boom.mp3", Sound.class);
                asset.load("etc/blank.png", Texture.class);
                break;
        }
        asset.finishLoading();
    }

    @Override
    public void render(float delta) {
        if(asset.update(17)) {
            switch(loadScene) {
                case LOBBY:
                    //TODO
                    break;
                case GAMEPLAY:
                    game.setScreen(new GameplayScreen());
                    break;
            }
        }
        
        batch.begin();
        banner.draw(batch);
        System.out.println(asset.getProgress());
        batch.end();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
    }
}