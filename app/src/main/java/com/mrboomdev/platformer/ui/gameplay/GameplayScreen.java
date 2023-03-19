package com.mrboomdev.platformer.ui.gameplay;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameplayScreen implements Screen {
	private SpriteBatch batch;
	
	public GameplayScreen() {
		this.batch = new SpriteBatch();
	}
	
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1);
		batch.begin();
		{
			
		}
		batch.end();
	}
	
	@Override
	public void show() {
		//INIT THE SCENE
	}
	
	@Override
	public void pause() {
		//TRIGGER PAUSE MENU
	}
	
	@Override
	public void resize(int width, int height) {
		//RECALCULATE THE SCREEN SIZE
	}
	
	@Override
	public void dispose() {
		//CLEAR ASSETS
	}
	
	@Override public void hide() {}
	@Override public void resume() {}
}