package com.mrboomdev.platformer.scenes.loading;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mrboomdev.platformer.environment.EnvironmentCreator;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.scenes.gameplay.GameplayScreen;

public class LoadingScreen extends CoreScreen {
    private final GameHolder game = GameHolder.getInstance();
	private final EnvironmentCreator environmentCreator;
    private final SpriteBatch batch;
	private final BitmapFont font;

    public LoadingScreen() {
        this.batch = new SpriteBatch();
		this.font = game.assets.get("loading.ttf");
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
		
		if(environmentCreator.getBareStatus() == EnvironmentCreator.Status.DONE && game.isReady()) {
			game.setScreen(new GameplayScreen(game.environment));
        }
    }
    
    @Override
    public void dispose() {
        batch.dispose();
    }
}