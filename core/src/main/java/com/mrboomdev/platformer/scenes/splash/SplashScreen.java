package com.mrboomdev.platformer.scenes.splash;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.util.AnimationUtil;

public class SplashScreen extends CoreScreen {
    private MainGame game;
    private SpriteBatch batch;
    private Sprite logo, gradient;
    private AnimationUtil fadeAnimation;
    private float progress = 0;
    
    public SplashScreen(MainGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
    }
    
    @Override
    public void show() {
        fadeAnimation = new AnimationUtil()
            .attach(logo).attach(gradient)
            .init((Sprite sprite, float progress) -> {
                sprite.setAlpha(0);
            })
            .handleUpdate((Sprite sprite, float progress) -> {
                sprite.setAlpha(1);
            });
    }
    
    @Override
    public void render(float delta) {
        progress += delta;
        fadeAnimation.update(progress);
    }
    
    @Override
    public void dispose() {
        batch.dispose();
    }
}