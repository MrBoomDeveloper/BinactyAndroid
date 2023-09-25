package com.mrboomdev.platformer.ui.gameplay.layout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ui.ActorUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class SubtitlesLayout extends ActorUtil {
	private final GameHolder game = GameHolder.getInstance();
	private Sound speechSound;
	private BitmapFont font;
	private final List<Subtitle> lines = new ArrayList<>();
	private int currentLine, wasCharacter;
	private float progress, fadeDuration;

	public Subtitle addLine(String text, float speed, float endDuration, Runnable callback) {
		var line = new Subtitle();
		line.text = text;
		line.endDuration = endDuration;
		line.speed = speed;
		line.callback = callback;
		lines.add(line);

		return line;
	}

	public void setSpeechSound(Sound sound) {
		this.speechSound = sound;
	}

	public Subtitle addLine(String text, float speed, float endDuration) {
		return addLine(text, speed, endDuration, null);
	}

	public Subtitle addLine(String text, float speed, Runnable callback) {
		return addLine(text, speed, -1, callback);
	}

	public Subtitle addLine(String text, Runnable callback) {
		return addLine(text, -1, -1, callback);
	}

	public Subtitle addLine(String text, float speed) {
		return addLine(text, speed, -1);
	}

	public Subtitle addLine(String text) {
		return addLine(text, -1);
	}

	public void setFadeDuration(float duration) {
		this.fadeDuration = duration;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(font == null) {
			tryToLoadResources();
			return;
		}

		var line = getCurrentLine();
		if(line == null) {
			progress = 0;
			return;
		}

		progress += Gdx.graphics.getDeltaTime();

		int currentCharacter = Math.min(
				line.text.length(),
				Math.round((line.text.length() / line.duration) * progress));

		if(wasCharacter != currentCharacter && speechSound != null) {
			speechSound.play(.1f);
		}

		wasCharacter = currentCharacter;

		if(line.callback != null && !line.didCallbackRan) {
			line.didCallbackRan = true;
			line.callback.run();
		}

		if(progress > line.duration + line.endDuration) {
			currentLine++;
			progress = 0;
		}

		if(progress < fadeDuration) {
			font.setColor(1, 1, 1, 1 / fadeDuration * progress);
		}

		float durationBeforeFade = line.duration + line.endDuration - fadeDuration;
		if(progress >= durationBeforeFade) {
			float alpha = 1 - (1 / fadeDuration * (progress - durationBeforeFade));
			font.setColor(1, 1, 1, alpha);
		}

		line.glyph.setText(font, line.text.substring(0, currentCharacter));

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

		if(line.endDuration == -1) {
			line.endDuration = 1.5f;
		}

		if(line.duration == -1) {
			var temp = line.text;

			for(var item : List.of("!", "?", " ", ".", "-", ",", "'", "\"", "/", "\\")) {
				temp.replace(item, "");
			}

			line.duration = temp.length() / 10f / line.speed;
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
		public float duration = -1, speed = 1, endDuration = 2;
		public Runnable callback;
		public boolean didCallbackRan;
	}
}