package com.mrboomdev.platformer.entity.bot;

import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.bot.ai.AiTargeter;
import com.mrboomdev.platformer.entity.character.CharacterBrain;
import com.mrboomdev.platformer.environment.path.presets.MapScanner;
import com.mrboomdev.platformer.game.GameHolder;

public class BotFollower extends CharacterBrain {
	private final GameHolder game = GameHolder.getInstance();
	private Runnable completionCallback, failureCallback;
	private String[] waypoints;
	private BotTarget target;
	private final AiTargeter targeter = new AiTargeter(this);
	private boolean isFinished;
	private int didAdditionalSteps;
	private float speed = 1;

	@Override
	public void start() {
		var tiles = game.environment.map.tilesMap.values();
		var graph = MapScanner.getGraphByWaypoints(tiles, waypoints);

		targeter.setGraph(graph);
	}

	@Override
	public void update() {
		if(isFinished || target == null) return;

		this.targeter.update();

		var owner = this.getEntity();
		var path = targeter.getPath(target);
		var nextPosition = targeter.getPower(path, target);

		if(target.getPosition().dst(owner.getPosition()) < .1f) {
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

		var power = nextPosition.cpy().sub(owner.getPosition()).scl(5);
		power.add((float)(Math.random() * 4) - 2, (float)(Math.random() * 4) - 2);
		owner.usePower(power, owner.stats.speed * speed);
		updateHoldingItem(power);
	}

	public void setTarget(float x, float y) {
		setTarget(new BotTarget.SimpleBotTarget(x, y));
	}

	public void setTarget(BotTarget target) {
		this.targeter.setTarget(target);
		this.target = target;
		this.isFinished = false;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setWaypoints(String[] waypoints) {
		this.waypoints = waypoints;
	}

	public void onCompleted(Runnable callback) {
		this.completionCallback = callback;
	}

	public void setCompletionListener(Runnable callback) {
		this.completionCallback = callback;
	}

	public void onFailed(Runnable callback) {
		this.failureCallback = callback;
	}
}