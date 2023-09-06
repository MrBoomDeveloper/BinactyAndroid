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

public class SubtitlesLayout extends ActorUtil {
	private final GameHolder game = GameHolder.getInstance();
	private Sound speechSound;
	private BitmapFont font;
	private final List<Subtitle> lines = new ArrayList<>();
	private int currentLine, wasCharacter, currentCharacter;
	private float progress;

	public void addLine(String text, float fadeDuration, float duration, Runnable callback) {
		var line = new Subtitle();
		line.text = text;
		line.fadeDuration = fadeDuration;
		line.duration = duration;
		line.callback = callback;
		lines.add(line);
	}

	public void addLine(String text, float fadeDuration, float duration) {
		addLine(text, fadeDuration, duration, null);
	}

	public void addLine(String text, float fadeDuration, Runnable callback) {
		addLine(text, fadeDuration, -1, callback);
	}

	public void addLine(String text, Runnable callback) {
		addLine(text, -1, -1, callback);
	}

	public void addLine(String text, float fadeDuration) {
		addLine(text, fadeDuration, -1);
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
		if(line == null) {
			progress = 0;
			return;
		}

		progress += Gdx.graphics.getDeltaTime();

		currentCharacter = Math.min(
				line.text.length(),
				Math.round(line.text.length() / line.duration * progress * 3));

		if(wasCharacter != currentCharacter) {
			if(speechSound == null) {
				speechSound = game.assets.get("audio/sounds/speech.wav");
			}

			//Uhm... when the right sound will be choosen, ill uncomment this line.
			//speechSound.play(.5f);
		}

		wasCharacter = currentCharacter;

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
		}

		float durationBeforeFade = line.duration - line.fadeDuration;
		if(progress >= durationBeforeFade) {
			float alpha = 1 - (1 / line.fadeDuration * (progress - durationBeforeFade));
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

		if(line.duration == -1) {
			var temp = line.text;

			for(var item : List.of("!", "?", " ", ".", "-", ",", "'", "\"", "/", "\\")) {
				temp.replace(item, "");
			}

			line.duration = temp.length() / 5f;
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
		public float duration = -1, fadeDuration = -1;
		public Runnable callback;
		public boolean didCallbackRan;
	}
}