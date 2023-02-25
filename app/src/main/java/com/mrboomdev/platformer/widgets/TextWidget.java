package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ActorUtil;

public class TextWidget extends ActorUtil {
	public float opacity = 1;
    private GlyphLayout glyph;
    private BitmapFont font;
    private String text = "";
    
    public TextWidget(String fontName) {
        this.font = GameHolder.getInstance().assets.get(fontName, BitmapFont.class);
		glyph = new GlyphLayout(font, text);
    }
    
    public TextWidget setText(String text) {
        this.text = text;
        this.glyph.setText(font, text);
		return this;
    }
	
	public TextWidget setOpacity(float opacity) {
		this.opacity = opacity;
		font.setColor(1, 1, 1, opacity);
		glyph.setText(font, text);
		return this;
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		font.draw(batch, glyph, getX() - (glyph.width / 2), getY());
	}
}