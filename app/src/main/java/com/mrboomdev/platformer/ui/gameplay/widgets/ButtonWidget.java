package com.mrboomdev.platformer.ui.gameplay.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ActorUtil;

public class ButtonWidget extends ActorUtil {
	private ShapeRenderer shapeRenderer;
	private Sprite backgroundImage, foregroundImage;
	private float foregroundWidth, foregroundHeight;
	private BitmapFont font;
	private GlyphLayout glyph;
	private Style style;
	
	public static final float inactiveBackgroundOpacity = .1f;
	public static final float activeBackgroundOpacity = .07f;
	public static final float inactiveForegroundOpacity = .8f;
	public static final float activeForegroundOpacity = .5f;

	public ButtonWidget(Style style) {
		this.style = style;
		this.shapeRenderer = new ShapeRenderer();
		this.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(foregroundImage != null) foregroundImage.setAlpha(activeForegroundOpacity);
				backgroundImage.setAlpha(activeBackgroundOpacity);
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if(foregroundImage != null) foregroundImage.setAlpha(inactiveForegroundOpacity);
				backgroundImage.setAlpha(inactiveBackgroundOpacity);
			}
		});
	}
	
	public ButtonWidget setBackgroundImage(Texture texture) {
		backgroundImage = new Sprite(texture);
		return this;
	}
	
	public ButtonWidget setForegroundImage(Texture texture, float width, float height) {
		foregroundWidth = width;
		foregroundHeight = height;
		
		foregroundImage = new Sprite(texture);
		foregroundImage.setSize(width, height);
		return this;
	}
	
	public ButtonWidget setText(String text, BitmapFont font) {
		this.font = font;
		this.glyph = new GlyphLayout(font, text);
		return this;
	}
	
	@Override
	public void act(float delta) {
		switch(style) {
			case BULLET:
				break;
			case CARD:
				break;
		}
	}

	@Override
	public void draw(Batch batch, float alpha) {
		switch(style) {
			case BULLET:
				break;
			case CARD:
				break;
		}
		
		if(backgroundImage != null) backgroundImage.draw(batch);
		if(foregroundImage != null) foregroundImage.draw(batch);
		if(font != null) font.draw(batch, glyph, 0, 0);
	}
	
	public enum Style {
		BULLET,
		CARD,
		TAB
	}
}