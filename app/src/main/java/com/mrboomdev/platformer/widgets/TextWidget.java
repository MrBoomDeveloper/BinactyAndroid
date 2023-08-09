package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ui.ActorUtil;

@SuppressWarnings("unused")
public class TextWidget extends ActorUtil {
    private final GlyphLayout glyph;
    private final BitmapFont font;
	public float opacity = 1;
	private Align hAlign, vAlign;
    private String text = "";

    public TextWidget(String fontName) {
		super();
		GameHolder game = GameHolder.getInstance();
		this.font = game.assets.get(fontName);
		this.glyph = new GlyphLayout(font, text);
		this.hAlign = Align.CENTER;
		this.vAlign = Align.CENTER;
    }
    
    public TextWidget setText(String text) {
        this.text = text;
        this.glyph.setText(font, text);
		font.setColor(1, 1, 1, opacity);
		return this;
    }
	
	public TextWidget setAlign(Align hAlign, Align vAlign) {
		this.hAlign = hAlign;
		this.vAlign = vAlign;
		return this;
	}
	
	public TextWidget setOpacity(float opacity) {
		this.opacity = opacity;
		return this;
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		super.update();

		font.setColor(1, 1, 1, opacity * getOpacity());
		glyph.setText(font, text);

		font.draw(batch, glyph, getX() + getOffset(hAlign, true), getY() + getOffset(vAlign, false));
	}
	
	public float getOffset(Align align, boolean isHorizontal) {
		if(align == null) return 0;
		switch(align) {
			case CENTER: return (isHorizontal ? -(glyph.width / 2) : -(glyph.height / 2));
			case RIGHT: return -glyph.width;
			case BOTTOM: return glyph.height;
			default: return 0;
		}
	}
}