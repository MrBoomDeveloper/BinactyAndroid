package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.game.GameHolder;
import java.util.TreeMap;
import com.mrboomdev.platformer.util.ActorUtil;
import java.util.Map.Entry;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class DebugValuesWidget extends ActorUtil {
    private BitmapFont font;
	private GlyphLayout glyph;
    private TreeMap<String, String> values = new TreeMap<>();
	private GLProfiler profiler;
    
    public DebugValuesWidget() {
		font = GameHolder.getInstance().assets.get("debug.ttf", BitmapFont.class);
		glyph = new GlyphLayout(font, "DEBUG WIDGET WAITING FOR UPDATES");
		profiler = new GLProfiler(Gdx.graphics);
		profiler.enable();
    }
    
    public void setValue(String key, String value) {
        values.put(key, value);
    }
	
	@Override
	public void act(float delta) {
		setValue("Screen Fps", String.valueOf(Gdx.graphics.getFramesPerSecond()));
		setValue("Screen Delta", String.valueOf(delta));
		setValue("Player Position", "[ " + (connectedEntity.getPosition().x) + " : " + (connectedEntity.getPosition().y) + " ]");
		setValue("Gl Total draws", String.valueOf(profiler.getDrawCalls()));
		setValue("Gl Texture bimdings", String.valueOf(profiler.getTextureBindings()));
		setValue("Gl Calls", String.valueOf(profiler.getCalls()));
		setValue("Gl Shader switches", String.valueOf(profiler.getShaderSwitches()));
		profiler.reset();
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		StringBuilder builder = new StringBuilder();
		for(Entry entry : values.entrySet()) {
			builder.append(entry.getKey() + ": ");
			builder.append(entry.getValue() + "\n");
		}
		glyph.setText(font, builder.toString());
		font.draw(batch, glyph, getX(), getY() - glyph.height);
	}
}