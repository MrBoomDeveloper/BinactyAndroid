package com.mrboomdev.platformer.ui.gameplay.layout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ui.ActorUtil;

import java.util.ArrayList;
import java.util.List;

public class SubtitlesLayout extends ActorUtil {
	private final GameHolder game = GameHolder.getInstance();
	private BitmapFont font;
	private final List<Subtitle> lines = new ArrayList<>();
	private int currentLine;
	private float progress;

	public void addLine(String text, float fadeDuration, float speed, Runnable callback) {
		var line = new Subtitle();
		line.text = text;
		line.fadeDuration = fadeDuration;
		line.speed = speed;
		line.callback = callback;
		lines.add(line);
	}

	public void addLine(String text, float fadeDuration, float speed) {
		addLine(text, fadeDuration, speed, null);
	}

	public void addLine(String text, float fadeDuration, Runnable callback) {
		addLine(text, fadeDuration, 1, callback);
	}

	public void addLine(String text, Runnable callback) {
		addLine(text, -1, 1, callback);
	}

	public void addLine(String text, float fadeDuration) {
		addLine(text, fadeDuration, 1);
	}

	public void addLine(String text) {
		addLine(text, -1);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(font == null) {
			tryToLoadResources();
			return;
		}

		var line = getCurrentLine();
		if(line == null) return;

		progress += Gdx.graphics.getDeltaTime() * line.speed;

		if(line.callback != null && !line.didCallbackRan) {
			line.didCallbackRan = true;
			line.callback.run();
		}

		if(progress > line.duration) {
			currentLine++;
			progress = 0;
		}

		if(progress < line.fadeDuration) {
			font.setColor(1, 1, 1, 1 / line.fadeDuration * progress);
			line.glyph.setText(font, line.text);
		}

		float durationBeforeFade = line.duration - line.fadeDuration;
		if(progress >= durationBeforeFade) {
			float alpha = 1 - (1 / line.fadeDuration * (progress - durationBeforeFade));
			font.setColor(1, 1, 1, alpha);
			line.glyph.setText(font, line.text);
		}

		font.draw(batch, line.glyph,
				Gdx.graphics.getWidth() / 2f - line.glyph.width / 2f,
				Gdx.graphics.getHeight() / 6f + line.glyph.height);
	}

	public Subtitle getCurrentLine() {
		if(currentLine >= lines.size()) return null;

		var line = lines.get(currentLine);

		if(line.glyph == null) {
			line.glyph = new GlyphLayout(font, line.text);
		}

		if(line.duration == -1) {
			line.duration = line.text.length() / 3f;
		}

		if(line.fadeDuration == -1) {
			line.fadeDuration = Math.min(2, line.duration / 4);
		}

		return line;
	}

	private void tryToLoadResources() {
		if(!game.assets.isLoaded("subtitles.ttf")) return;

		font = game.assets.get("subtitles.ttf");
	}

	public static class Subtitle {
		public String text;
		public GlyphLayout glyph;
		public float duration = -1, fadeDuration = -1, speed = 1;
		public Runnable callback;
		public boolean didCallbackRan;
	}
}