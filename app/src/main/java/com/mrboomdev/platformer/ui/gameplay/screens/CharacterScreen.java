package com.mrboomdev.platformer.ui.gameplay.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mrboomdev.platformer.util.io.FileUtil;

public class CharacterScreen extends Game {
	private SpriteBatch batch;
	private Sprite testSprite;

    @Override
    public void create() {
		batch = new SpriteBatch();
		testSprite = new Sprite(new Texture(FileUtil.internal("ui/banner/loading.jpg").getFileHandle()));
	}

    @Override
    public void render() {
		ScreenUtils.clear(0, 0, 0, 1);
		batch.begin();
		testSprite.draw(batch);
		batch.end();
    }
}