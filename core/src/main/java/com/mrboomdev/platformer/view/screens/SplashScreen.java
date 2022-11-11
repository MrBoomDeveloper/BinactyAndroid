package com.mrboomdev.platformer.view.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SplashScreen extends Game {
	private SpriteBatch batch;
	private Texture logo = new Texture(Gdx.files.internal("banner/logo/mrboomdev.png"));

    @Override
    public void create() {
		
	}
	
	public void render() {
		batch.begin();
		
		batch.draw(logo, 0, 0, 100, 100);
		
		batch.end();
	}
}
