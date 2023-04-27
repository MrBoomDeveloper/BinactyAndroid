package com.mrboomdev.platformer.script.bridge;

import com.badlogic.gdx.Gdx;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.widgets.TextWidget;

public class UiBridge {
	private GameHolder game = GameHolder.getInstance();
	private UiListener listener;
	
	public void setListener(UiListener listener) {
		this.listener = listener;
	}
	
	public void callListener(Function function) {
		if(listener == null) return;
		switch(function) {
			case TIMER_END: {
				listener.timerEnd();
				return;
			}
		}
	}
	
	public TextWidget createText(String font) {
		return new TextWidget(font).setOpacity(1).addTo(game.environment.stage);
	}
	
	public float getWidth() {
		return Gdx.graphics.getWidth();
	}
	
	public float getHeight() {
		return Gdx.graphics.getHeight();
	}
	
	public void setTitle(String text, float duration) {
		
	}
	
	public void setTimer(float initial, float speed, boolean isCountdown) {
		
	}
	
	public void setFade(float from, float to) {
		
	}
	
	public interface UiListener {
		void timerEnd();
		void timerNextSecond();
	}
	
	public enum Function {
		TIMER_END
	}
}