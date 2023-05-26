package com.mrboomdev.platformer.environment.editor.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ActorUtil;

public class ButtonWidget extends ActorUtil {
	private final Sprite sprite;
	private final ShapeRenderer shape;
	private final BitmapFont font;
	private final GlyphLayout glyph;
	
	public ButtonWidget(Sprite sprite, String title) {
		this.sprite = new Sprite(sprite);
		this.shape = new ShapeRenderer();
		this.setSize(100, 100);
		this.sprite.setSize(getWidth(), getHeight());
		GameHolder game = GameHolder.getInstance();
		this.font = game.assets.get("buttonWhite.ttf");
		this.glyph = new GlyphLayout();
		this.glyph.setText(font, title, Color.WHITE, 90, Align.left, true);
	}

    @Override
    public void draw(Batch batch, float alpha) {
		batch.end();
        shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setColor(0, 0, 0, 1);
		shape.rect(getX(), getY(), getWidth(), getHeight());
		shape.end();
		batch.begin();
		
		sprite.setCenter(getX() + getWidth() / 2, getY() + getHeight() / 2);
		sprite.draw(batch);
		font.draw(batch, glyph, getX(), getY() + glyph.height);
    }
}