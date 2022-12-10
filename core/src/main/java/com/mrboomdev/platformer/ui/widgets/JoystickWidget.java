package com.mrboomdev.platformer.ui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class JoystickWidget extends Actor {
	
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, 1);
		batch.draw(new Texture(Gdx.files.internal("img/ui/attack.jpg")), 0, 0);
	}
}