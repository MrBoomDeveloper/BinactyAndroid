package com.mrboomdev.platformer.environment.editor.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mrboomdev.platformer.util.ActorUtil;

public class ButtonWidget extends ActorUtil {
	private Sprite sprite;
	private ShapeRenderer shape;
	
	public ButtonWidget(Sprite sprite) {
		this.sprite = new Sprite(sprite);
		this.shape = new ShapeRenderer();
		this.setSize(100, 100);
		this.sprite.setSize(getWidth(), getHeight());
	}

    @Override
    public void draw(Batch batch, float alpha) {
		batch.end();
        shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setColor(1, 1, 1, 1);
		shape.rect(getX(), getY(), getWidth(), getHeight());
		shape.end();
		batch.begin();
		if(sprite != null) {
			sprite.setCenter(getX() + getWidth() / 2, getY() + getHeight() / 2);
			sprite.draw(batch);
		}
    }
}