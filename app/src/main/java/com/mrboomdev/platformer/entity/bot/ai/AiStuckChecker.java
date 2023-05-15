package com.mrboomdev.platformer.entity.bot.ai;

import androidx.annotation.NonNull;

import com.badlogic.gdx.math.Vector2;

public class AiStuckChecker {
	private Vector2 lastCheckPosition;
	private long lastCheckTime;

	public void setDestination(Vector2 destinationPosition, float expectedSpeed) {
		reset();
	}
	
	public boolean isStuck(Vector2 currentPosition) {
		if(lastCheckPosition == null) {
			update(currentPosition);
			return false;
		}
		if(System.currentTimeMillis() > lastCheckTime + 1500) {
			if(currentPosition.dst(lastCheckPosition) < .5f) return true;
			update(currentPosition);
		}
		return false;
	}
	
	public void reset() {
		lastCheckPosition = null;
		lastCheckTime = 0;
	}
	
	private void update(@NonNull Vector2 position) {
		lastCheckPosition = position.cpy();
		lastCheckTime = System.currentTimeMillis();
	}
}