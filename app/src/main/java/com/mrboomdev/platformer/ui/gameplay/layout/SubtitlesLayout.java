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
	private float progress, fadeDuration, speed, endDuration;
	private boolean letterByLetter;

	public SubtitlesLayout() {
		setDefaultSpeed(.9f);
		setDefaultEndDuration(2);
		setDefaultFadeDuration(.5f);
		setLetterByLetterEffectEnabled(true);
	}

	public void setLetterByLetterEffectEnabled(boolean enable) {
		this.letterByLetter = enable;
	}

	public void setDefaultSpeed(float speed) {
		this.speed = speed;
	}

	public void setDefaultEndDuration(float endDuration) {
		this.endDuration = endDuration;
	}

	public void setDefaultFadeDuration(float duration) {
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

		if(progress < line.fadeDuration) {
			font.setColor(1, 1, 1, Math.min(1, 1 / line.fadeDuration * progress));
		}

		float durationBeforeFade = line.duration + line.endDuration - line.fadeDuration;
		if(progress >= durationBeforeFade) {
			float alpha = 1 - (1 / line.fadeDuration * (progress - durationBeforeFade));
			font.setColor(1, 1, 1, alpha);
		}

		line.glyph.setText(font, letterByLetter ? line.text.substring(0, currentCharacter) : line.text);

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

		if(line.speed == -1) {
			line.speed = speed;
		}

		if(line.endDuration == -1) {
			line.endDuration = (line.duration == -1) ? endDuration : 0;
		}

		if(line.fadeDuration == -1) {
			line.fadeDuration = fadeDuration;
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

	public Subtitle addLine() {
		return new Subtitle(this);
	}

	public static class Subtitle {
		protected String text;
		protected GlyphLayout glyph;
		protected float duration = -1, speed = -1, endDuration = -1, fadeDuration = -1;
		protected Runnable callback;
		protected boolean didCallbackRan;
		private SubtitlesLayout owner;

		public Subtitle() {}

		public Subtitle(SubtitlesLayout owner) {
			this.owner = owner;
		}

		public Subtitle setText(String text) {
			this.text = text;
			return this;
		}

		public Subtitle setDuration(float duration) {
			this.duration = duration;
			return this;
		}

		public Subtitle setSpeed(float speed) {
			this.speed = speed;
			return this;
		}

		public Subtitle setEndDuration(float endDuration) {
			this.endDuration = endDuration;
			return this;
		}

		public Subtitle setFadeDuration(float fadeDuration) {
			this.fadeDuration = fadeDuration;
			return this;
		}

		public Subtitle setStartCallback(Runnable callback) {
			this.callback = callback;
			return this;
		}

		public void build() {
			owner.lines.add(this);
		}
	}
}