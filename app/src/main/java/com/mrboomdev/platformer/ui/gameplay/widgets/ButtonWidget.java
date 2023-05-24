package com.mrboomdev.platformer.ui.gameplay.widgets;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ActorUtil;

public class ButtonWidget extends ActorUtil {
	private final ShapeRenderer shape;
	private Sprite backgroundImage, foregroundImage;
	private BitmapFont font;
	private GlyphLayout glyph;
	private final Style style;
	private float padding;
	private boolean isPressed, highlight;
	
	public static final float inactiveBackgroundOpacity = .8f;
	public static final float activeBackgroundOpacity = .25f;
	public static final float inactiveForegroundOpacity = .8f;
	public static final float activeForegroundOpacity = .5f;
	public static final float COMPACT_HEIGHT = 45;
	public static final float BULLET_HEIGHT = 50;

	public ButtonWidget(@NonNull Style style) {
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
				break;

			case CARD:
				setHeight(75);
				setWidth(60);
				padding = 5;
				break;

			case COMPACT:
				setHeight(COMPACT_HEIGHT);
				padding = 8;
				break;
		}
	}

	@SuppressWarnings("unused")
	public ButtonWidget setBackgroundImage(Sprite sprite) {
		if(sprite == null) {
			backgroundImage = null;
			return this;
		}
		backgroundImage = new Sprite(sprite);
		return this;
	}
	
	public ButtonWidget setForegroundImage(Sprite sprite) {
		foregroundImage = sprite == null ? null : new Sprite(sprite);
		if(sprite == null) return this;
		if(style == Style.COMPACT) return this;

		float proportion = (foregroundImage.getHeight() > foregroundImage.getWidth())
			? ((getHeight() - padding * 5) / foregroundImage.getHeight())
			: ((getWidth() - padding * 5) / foregroundImage.getWidth());
			
		foregroundImage.setSize(foregroundImage.getWidth() * proportion, foregroundImage.getHeight() * proportion);
		return this;
	}
	
	public ButtonWidget setText(String text, BitmapFont font) {
		this.font = font;
		this.glyph = new GlyphLayout(font, text);
		return this;
	}

	public ButtonWidget setText(String text) {
		var assets = GameHolder.getInstance().assets;
		switch(style) {
			case COMPACT: return setText(text, assets.get("compact_button_label.ttf"));
			case CARD: return this;
			default: return setText(text, assets.get("bulletButton.ttf"));
		}
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	@Override
	public void draw(Batch batch, float alpha) {
		float[] textPosition = new float[]{getX(), getY()};
		switch(style) {
			case BULLET: {
				if(foregroundImage != null) {
					foregroundImage.setPosition(getX() + padding * 1.5f, getY() + padding);
					setWidth(glyph.width + foregroundImage.getWidth() + padding * 6);
					textPosition[0] += padding * 3 + foregroundImage.getWidth();
				} else {
					setWidth(getHeight() * 2 + glyph.width);
					textPosition[0] += getWidth() / 2 - glyph.width / 2;
				}
				textPosition[1] += getHeight() / 2 + glyph.height / 2;
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
			} break;
				
			case CARD: {
				if(backgroundImage != null) {
					backgroundImage.setPosition(getX(), getY());
					backgroundImage.setSize(getWidth(), getHeight());
					backgroundImage.setAlpha(getColor().a);
				}
				if(foregroundImage != null) {
					foregroundImage.setCenter(getX() + getWidth() / 2, getY() + getHeight() / 2);
					foregroundImage.setAlpha(getColor().a);
				}
			} break;

			case COMPACT: {
				textPosition[1] += padding + glyph.height / 2 + 16;
				if(foregroundImage != null) {
					var proportion = getHeight() / foregroundImage.getHeight();
					foregroundImage.setSize(
							foregroundImage.getWidth() * proportion - padding * 2,
							foregroundImage.getHeight() * proportion - padding * 2);

					foregroundImage.setPosition(getX() + padding, getY() + padding);
					textPosition[0] += foregroundImage.getWidth() + padding * 3;
					setWidth(foregroundImage.getWidth() + glyph.width + padding * 6);
				} else {
					textPosition[0] += 60 - glyph.width / 2;
					setWidth(120);
				}

				batch.end();
				shape.begin(ShapeRenderer.ShapeType.Filled); {
					float color = isPressed ? .3f : .2f;
					if(highlight) color += .15f;

					shape.setColor(color + .2f, color + .2f, color + .2f, 1);
					shape.rect(getX(), getY(), getWidth(), getHeight());

					shape.setColor(color, color, color, 1);
					shape.rect(getX() + 2, getY() + 2, getWidth() - 4, getHeight() - 4);
				} shape.end();
				batch.begin();
			} break;
		}
		
		if(backgroundImage != null) backgroundImage.draw(batch);
		if(foregroundImage != null) foregroundImage.draw(batch);
		if(font != null) font.draw(batch, glyph, textPosition[0], textPosition[1]);
	}
	
	public enum Style {
		BULLET,
		CARD,
		TAB,
		COMPACT
	}
}