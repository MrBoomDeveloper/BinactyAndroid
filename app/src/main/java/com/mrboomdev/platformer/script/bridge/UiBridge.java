package com.mrboomdev.platformer.script.bridge;

import com.badlogic.gdx.Gdx;
import com.mrboomdev.platformer.environment.gamemode.GamemodeFunction;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.widgets.FadeWidget;
import com.mrboomdev.platformer.widgets.TextWidget;

@SuppressWarnings("unused")
public class UiBridge {
	private final GameHolder game = GameHolder.getInstance();
	private UiListener listener;
	
	public void setListener(UiListener listener) {
		this.listener = listener;
	}
	
	public void callListener(Function function) {
		if(listener == null) return;
		listener.timerEnd();
	}

	public void setVisibility(boolean isVisible) {
		game.settings.isUiVisible = isVisible;
	}
	
	public TextWidget createText(String font, String text) {
		return new TextWidget(font).setOpacity(1).setText(text).addTo(game.environment.stage);
	}

	public Object createImage(String path) {
		return null;
	}

	public FadeWidget createFade(float initialOpacity) {
		return new FadeWidget(initialOpacity).addTo(game.environment.stage);
	}

	public void createTimer(int initial, float speed) {
		var options = new GamemodeFunction.Options() {{
			this.time = initial;
		}};

		var fun = new GamemodeFunction(GamemodeFunction.Action.TIMER_SETUP, options);
		fun.speed = speed;

		game.environment.gamemode.runFunction(fun);
	}

	public void createTitle(String message, float duration) {
		var options = new GamemodeFunction.Options() {{
			this.text = message;
		}};

		var fun = new GamemodeFunction(GamemodeFunction.Action.TITLE, options);
		fun.duration = duration;
		fun.isLong = true;

		game.environment.gamemode.runFunction(fun);
	}
	
	public float getWidth() {
		return Gdx.graphics.getWidth();
	}
	
	public float getHeight() {
		return Gdx.graphics.getHeight();
	}
	
	public void setTitle(String text, float duration) {
		
	}
	
	public interface UiListener {
		void timerEnd();
	}
	
	public enum Function {
		TIMER_END
	}
}