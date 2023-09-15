package com.mrboomdev.platformer.entity.bot.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.bot.BotTarget;
import com.mrboomdev.platformer.entity.character.CharacterBrain;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;

@SuppressWarnings("unused")
public class AiStuckChecker {
	private BotTarget lastSeenWall;
	private Vector2 destinationPosition, lastGridPosition;
	private float checkReloadProgress, lastCheckedDistance;
	private float maxDistanceFromLastSeenWall, lookingAtSameWallTime;
	private float estimatedTime, timePassed;
	private int failedChecksCount;
	private final CharacterBrain brain;
	private boolean doRememberThatStuck;
	private final GameHolder game = GameHolder.getInstance();

	public AiStuckChecker(CharacterBrain brain) {
		this.brain = brain;
	}

	public void setDestination(Vector2 destinationPosition, float estimatedTime) {
		if(this.destinationPosition != null) {
			if(destinationPosition == this.destinationPosition) return;
			if(destinationPosition.dst(this.destinationPosition) < 2.5f) return;
		}

		this.reset();

		this.destinationPosition = destinationPosition;
		this.estimatedTime = estimatedTime;
	}
	
	public boolean isStuck() {
		if(failedChecksCount >= 4) return true;

		var myPosition = brain.getEntity().getPosition();

		if(timePassed > estimatedTime && myPosition.dst(destinationPosition) > 1) {
			return true;
		}

		game.environment.world.rayCast((fixture, point, normal, fraction) -> {
			var position = brain.getEntity().getPosition();
			var data = fixture.getBody().getUserData();

			if(data instanceof MapTile) {
				var a = (MapTile)data;
				float distance = a.getPosition().dst(position);

				if(lastSeenWall == a) {
					if(distance > maxDistanceFromLastSeenWall) {
						maxDistanceFromLastSeenWall = distance;
						lookingAtSameWallTime = 0;
					} else {
						lookingAtSameWallTime += Gdx.graphics.getDeltaTime();
						doRememberThatStuck = (lookingAtSameWallTime > 2);
					}
				}

				if(distance < 2) {
					this.lastSeenWall = a;
					return 0;
				}
			}

			if(data instanceof BotTarget) {
				var a = (BotTarget)data;
				float distance = a.getPosition().dst(position);

				if(distance > 2) {
					this.doRememberThatStuck = false;
					return 0;
				}
			}

			return 1;
		}, brain.getEntity().getPosition(), destinationPosition);

		if(lookingAtSameWallTime > 2) return true;

		return doRememberThatStuck;
	}
	
	public void reset() {
		this.checkReloadProgress = 0;
		this.failedChecksCount = 0;

		this.lastCheckedDistance = 0;
		this.maxDistanceFromLastSeenWall = 0;

		this.lookingAtSameWallTime = 0;
		this.estimatedTime = 0;
		this.timePassed = 0;

		this.destinationPosition = null;
		this.lastSeenWall = null;

		this.doRememberThatStuck = false;
	}
	
	public void update() {
		var currentPosition = brain.getEntity().getPosition();
		float delta = Gdx.graphics.getDeltaTime();

		if(this.failedChecksCount < 0) failedChecksCount = 0;

		this.checkReloadProgress += delta;
		this.timePassed += delta;

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