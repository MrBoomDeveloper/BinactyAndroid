package com.mrboomdev.platformer.script.bridge;

import com.mrboomdev.platformer.game.GameHolder;

public class UiBridge {
	private GameHolder game = GameHolder.getInstance();
	private UiListener listener;
	
	public void setListener(UiListener listener) {
		this.listener = listener;
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
}