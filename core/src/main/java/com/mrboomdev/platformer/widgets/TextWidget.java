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
import com.mrboomdev.platformer.MainGame;

public class TextWidget extends Actor {
    private GlyphLayout glyph;
    private BitmapFont font;
    private String text = "";
    
    public TextWidget(String fontName) {
        this.font = MainGame.getInstance().asset.get(fontName, BitmapFont.class);
		glyph = new GlyphLayout(font, text);
    }
    
    public void setText(String text) {
        this.text = text;
        this.glyph.setText(font, text);
    }
	
	@Override
	public void draw(Batch batch, float opacity) {
		font.draw(batch, glyph, getX() - (glyph.width / 2), getY());
	}
}