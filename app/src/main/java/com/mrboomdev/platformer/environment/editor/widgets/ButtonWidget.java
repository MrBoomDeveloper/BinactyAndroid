package com.mrboomdev.platformer.environment.editor.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mrboomdev.platformer.util.ActorUtil;

public class ButtonWidget extends ActorUtil {
    private String text;
	private ShapeRenderer shape;

    public ButtonWidget(String text) {
        this.text = text;
		this.shape = new ShapeRenderer();
		this.setSize(200, 100);
    }

    @Override
    public void draw(Batch batch, float alpha) {
		batch.end();
        shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setColor(1, 1, 1, 1);
		shape.rect(getX(), getY(), getWidth(), getHeight());
		shape.end();
		batch.begin();
    }
}