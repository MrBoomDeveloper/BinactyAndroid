package com.mrboomdev.platformer.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ActorUtil;

import java.util.TreeMap;

public class DebugValuesWidget extends ActorUtil {
    private final BitmapFont font;
	private final GlyphLayout glyph;
    private final TreeMap<String, String> values = new TreeMap<>();
	private final GLProfiler profiler;
    
    public DebugValuesWidget() {
		super();
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
		var game = GameHolder.getInstance();
		var camera = game.environment.camera;
		var entityPosition = connectedEntity.getPosition();

		setValue("Assets loaded", "[ Internal: " + game.assets.getLoadedAssets() + ", External: " + game.externalAssets.getLoadedAssets() + " ]");

		setValue("Screen Fps", String.valueOf(Gdx.graphics.getFramesPerSecond()));

		setValue("Player Position", "[ " + entityPosition.x + " : " + entityPosition.y + " ]");

		setValue("Camera position", "[ " + camera.position.x + " : " + camera.position.y + " : " + camera.zoom + " ]");

		setValue("Gl Total draws", String.valueOf(profiler.getDrawCalls()));
		setValue("Gl Texture bindings", String.valueOf(profiler.getTextureBindings()));
		setValue("Gl Calls", String.valueOf(profiler.getCalls()));
		setValue("Gl Shader switches", String.valueOf(profiler.getShaderSwitches()));
		profiler.reset();
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		super.update();
		StringBuilder builder = new StringBuilder();
		for(var entry : values.entrySet()) {
			builder.append(entry.getKey());
			builder.append(": ");
			builder.append(entry.getValue());
			builder.append("\n");
		}
		glyph.setText(font, builder.toString());
		font.draw(batch, glyph, getX(), getY() - glyph.height);
	}
}