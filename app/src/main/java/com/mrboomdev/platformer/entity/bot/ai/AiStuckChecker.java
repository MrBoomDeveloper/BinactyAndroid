package com.mrboomdev.platformer.entity.bot.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

@SuppressWarnings("unused")
public class AiStuckChecker {
	private Vector2 destinationPosition;
	private float checkReloadProgress, lastCheckedDistance;
	private int failedChecksCount;

	public void setDestination(Vector2 destinationPosition) {
		if(this.destinationPosition != null) {
			if(destinationPosition == this.destinationPosition) return;
			if(destinationPosition.dst(this.destinationPosition) < 2.5f) return;
		}

		this.reset();
		this.destinationPosition = destinationPosition;
	}
	
	public boolean isStuck() {
		return (failedChecksCount >= 5);
	}
	
	public void reset() {
		this.checkReloadProgress = 0;
		this.failedChecksCount = 0;
		this.destinationPosition = null;
		this.lastCheckedDistance = 0;
	}
	
	public void update(Vector2 currentPosition) {
		if(this.failedChecksCount < 0) failedChecksCount = 0;

		this.checkReloadProgress += Gdx.graphics.getDeltaTime();

		if(this.lastCheckedDistance == 0) {
			this.lastCheckedDistance = destinationPosition.dst(currentPosition);
			return;
		}

		if(this.checkReloadProgress < 1) return;
		this.checkReloadProgress = 0;

		if(destinationPosition.dst(currentPosition) < (lastCheckedDistance - (lastCheckedDistance / 5))) {
			this.lastCheckedDistance = destinationPosition.dst(currentPosition);
			this.failedChecksCount -= 1;
		} else {
			this.failedChecksCount += 1;
		}
	}
}