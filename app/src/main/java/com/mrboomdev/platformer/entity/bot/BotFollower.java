package com.mrboomdev.platformer.entity.bot;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.bot.ai.AiTargeter;
import com.mrboomdev.platformer.entity.character.CharacterBrain;
import com.mrboomdev.platformer.entity.character.CharacterProgrammable;
import com.mrboomdev.platformer.environment.path.presets.MapScanner;
import com.mrboomdev.platformer.game.GameHolder;

public class BotFollower extends CharacterBrain {
	private final GameHolder game = GameHolder.getInstance();
	private Runnable completionCallback, failureCallback;
	private String[] waypoints;
	private BotTarget target;
	private final AiTargeter targeter = new AiTargeter(this);
	private boolean isFinished, stopOnReach = true, goStraightToTarget;
	private Vector2 randomPower = new Vector2();
	private int didAdditionalSteps;
	private float speed = 1, changeRandomPowerDelay;

	public BotFollower() {}

	public BotFollower(CharacterProgrammable owner) {
		setEntity(owner);
	}

	@Override
	public void start() {
		var tiles = game.environment.map.tilesMap.values();
		var graph = MapScanner.getGraphByWaypoints(tiles, waypoints);

		targeter.setGraph(graph);
	}

	@Override
	public void update() {
		if(target == null) return;

		var owner = this.getEntity();

		if(isFinished && stopOnReach) {
			updateHoldingItem();
			owner.usePower(Vector2.Zero, 0);
			return;
		}

		this.targeter.update();
		var path = targeter.getPath(target);
		var nextPosition = targeter.getPower(path, target);

		var targetPosition = target.getPosition();
		var ownerPosition = owner.getPosition();
		float distance = targetPosition.dst(ownerPosition);

		if(distance > .1f && (goStraightToTarget || distance < 1.5f)) {
			goDirectlyTo(ownerPosition, targetPosition, false);
			return;
		}

		if(distance < .1f) {
			isFinished = true;

			if(completionCallback != null) {
				completionCallback.run();
				isFinished = true;
			}

			owner.usePower(Vector2.Zero, 0);
			updateHoldingItem();

			return;
		}

		if(nextPosition == null) {
			if(didAdditionalSteps < 25) {
				owner.usePower(owner.wasPower, 1 * speed);
				updateHoldingItem(owner.wasPower);
				didAdditionalSteps++;
				return;
			}

			if(completionCallback != null) {
				completionCallback.run();
				isFinished = true;
			}

			owner.usePower(Vector2.Zero, 0);
			updateHoldingItem();
			return;
		}

		if(changeRandomPowerDelay <= 0) {
			float range = 2;

			randomPower.set(
					(float)(Math.random() * 2 * range) - range,
					(float)(Math.random() * 2 * range) - range
			);

			changeRandomPowerDelay = 1;
		} else {
			changeRandomPowerDelay -= Gdx.graphics.getDeltaTime();
		}

		goDirectlyTo(ownerPosition, nextPosition, true);
	}

	private void goDirectlyTo(Vector2 from, @NonNull Vector2 to, boolean useRandomPower) {
		var owner = getEntity();

		var power = to.cpy().sub(from).scl(5);
		if(useRandomPower) power.add(randomPower);

		owner.usePower(power, owner.stats.speed * speed);
		updateHoldingItem(power);
	}

	public void setGoStraightToTarget(boolean enable) {
		this.goStraightToTarget = enable;
	}

	public void setTarget(float x, float y) {
		setTarget(new BotTarget.SimpleBotTarget(x, y));
	}

	public void setTarget(BotTarget target) {
		this.targeter.setTarget(target);
		this.target = target;
		this.isFinished = false;
	}

	public void setStopOnReach(boolean isStop) {
		this.stopOnReach = isStop;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setWaypoints(String[] waypoints) {
		this.waypoints = waypoints;
	}

	public void setCompletionListener(Runnable callback) {
		this.completionCallback = callback;
	}

	public void setFailureListener(Runnable callback) {
		this.failureCallback = callback;
	}
}