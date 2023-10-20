package com.mrboomdev.platformer.scenes.loading;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mrboomdev.platformer.environment.EnvironmentCreator;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.core.CoreScreen;

public class LoadingScreen extends CoreScreen {
	private final EnvironmentCreator environmentCreator;
    private final SpriteBatch batch;
	private final BitmapFont font;

    public LoadingScreen() {
		var game = GameHolder.getInstance();
		this.font = game.assets.get("loading.ttf");

        this.batch = new SpriteBatch();
		this.environmentCreator = new EnvironmentCreator();
    }

    @Override
    public void show() {
		environmentCreator.start();
    }

    @Override
    public void render(float delta) {
		environmentCreator.ping();

		batch.begin(); {
			font.draw(batch, environmentCreator.getStatus(), 50, 75);
		} batch.end();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
    }
}