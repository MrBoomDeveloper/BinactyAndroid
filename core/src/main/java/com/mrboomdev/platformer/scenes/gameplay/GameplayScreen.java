package com.mrboomdev.platformer.scenes.gameplay;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.scenes.gameplay.GameplayUi;

public class GameplayScreen extends CoreScreen {
    private SpriteBatch batch;
    private GameplayUi ui;
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        ui = new GameplayUi();
    }
    
    @Override
    public void render(float delta) {
        ui.render(batch);
    }
    
    @Override
    public void dispose() {
        ui.dispose();
    }
}