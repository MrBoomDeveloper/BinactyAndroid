package com.mrboomdev.platformer.ui.gameplay.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mrboomdev.platformer.util.ActorUtil;

public class ButtonWidget extends ActorUtil {
	private ShapeRenderer shape;
	private FrameBuffer frameBuffer;
	private Sprite backgroundImage, foregroundImage;
	private BitmapFont font;
	private GlyphLayout glyph;
	private Style style;
	private float padding, height;
	private boolean isPressed;
	
	public static final float inactiveBackgroundOpacity = .1f;
	public static final float activeBackgroundOpacity = .07f;
	public static final float inactiveForegroundOpacity = .8f;
	public static final float activeForegroundOpacity = .5f;
	
	public static final float BULLET_HEIGHT = 50;

	public ButtonWidget(Style style) {
		this.style = style;
		this.shape = new ShapeRenderer();
		this.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(foregroundImage != null) foregroundImage.setAlpha(activeForegroundOpacity);
				if(backgroundImage != null) backgroundImage.setAlpha(activeBackgroundOpacity);
				isPressed = true;
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if(foregroundImage != null) foregroundImage.setAlpha(inactiveForegroundOpacity);
				if(backgroundImage != null) backgroundImage.setAlpha(inactiveBackgroundOpacity);
				isPressed = false;
			}
		});
		
		switch(style) {
			case BULLET:
				setHeight(BULLET_HEIGHT);
				padding = 10;
				height = BULLET_HEIGHT;
				break;
			case CARD:
				setHeight(75);
				setWidth(60);
				break;
		}
	}
	
	public ButtonWidget setBackgroundImage(Texture texture) {
		backgroundImage = new Sprite(texture);
		return this;
	}
	
	public ButtonWidget setForegroundImage(Sprite sprite) {
		foregroundImage = new Sprite(sprite);
		float proportion = (getHeight() - padding * 2) / foregroundImage.getHeight();
		foregroundImage.setSize(foregroundImage.getWidth() * proportion, foregroundImage.getHeight() * proportion);
		return this;
	}
	
	public ButtonWidget setText(String text, BitmapFont font) {
		this.font = font;
		this.glyph = new GlyphLayout(font, text);
		return this;
	}

	@Override
	public void draw(Batch batch, float alpha) {
		float[] textPosition = new float[2];
		switch(style) {
			case BULLET:
				if(foregroundImage != null) {
					foregroundImage.setPosition(getX() + padding * 1.5f, getY() + padding);
					setWidth(glyph.width + foregroundImage.getWidth() + padding * 6);
					textPosition[0] = getX() + padding * 3 + foregroundImage.getWidth();
				} else {
					setWidth(getHeight() * 2 + glyph.width);
					textPosition[0] = getX() + getWidth() / 2 - glyph.width / 2;
				}
				textPosition[1] = getY() + getHeight() / 2 + glyph.height / 2;
				{
					batch.end();
					Gdx.gl.glEnable(GL20.GL_BLEND);
					shape.begin(ShapeRenderer.ShapeType.Filled);
					shape.setColor(1, 1, 1, (isPressed ? .9f : 1));
					shape.circle(getX() + getHeight() / 2, getY() + getHeight() / 2, getHeight() / 2);
					shape.circle(getX() + getWidth() - getHeight() / 2, getY() + getHeight() / 2, getHeight() / 2);
					shape.rect(getX() + getHeight() / 2, getY(), getWidth() - getHeight(), getHeight());
					shape.end();
					Gdx.gl.glDisable(GL20.GL_BLEND);
					batch.begin();
				}
				break;
			case CARD:
				
				break;
		}
		
		if(backgroundImage != null) backgroundImage.draw(batch);
		if(foregroundImage != null) foregroundImage.draw(batch);
		if(font != null) font.draw(batch, glyph, textPosition[0], textPosition[1]);
	}
	
	public enum Style {
		BULLET,
		CARD,
		TAB
	}
	
	public static class NewButtonWidget extends ButtonWidget {
		public NewButtonWidget(Style style) {
			super(style);
		}
	}
}