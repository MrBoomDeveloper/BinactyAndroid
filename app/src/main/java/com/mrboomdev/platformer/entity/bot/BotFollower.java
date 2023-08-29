package com.mrboomdev.platformer.entity.bot;

import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.bot.ai.AiTargeter;
import com.mrboomdev.platformer.entity.character.CharacterBrain;
import com.mrboomdev.platformer.environment.path.presets.MapScanner;
import com.mrboomdev.platformer.game.GameHolder;

import java.util.Arrays;

public class BotFollower extends CharacterBrain {
	private final GameHolder game = GameHolder.getInstance();
	private Runnable completionCallback, failureCallback;
	private String[] waypoints;
	private BotTarget target;
	private final AiTargeter targeter = new AiTargeter(this);
	private final MapScanner mapScanner = new MapScanner();
	private boolean isFinished;
	private int didAdditionalSteps;

	@Override
	public void start() {
		var graph = mapScanner.getGraph(game.environment.map.tilesMap.values(), tile -> {
			var stream = Arrays.stream(waypoints);
			return stream.anyMatch(item -> tile.name.equals(item));
		});

		targeter.setGraph(graph);
	}

	@Override
	public void update() {
		if(isFinished || target == null) return;

		this.targeter.update();

		var owner = this.getEntity();
		var nextPosition = targeter.getPathTo(target);

		if(target.getPosition().dst(owner.getPosition()) < .2f) {
			isFinished = true;
			if(completionCallback != null) completionCallback.run();
			owner.usePower(Vector2.Zero, 0);
			return;
		}

		if(nextPosition == null) {
			if(didAdditionalSteps < 25) {
				owner.usePower(owner.wasPower, 1);
				didAdditionalSteps++;
				return;
			}

			if(completionCallback != null) {
				completionCallback.run();
				isFinished = true;
			}

			owner.usePower(Vector2.Zero, 0);
			return;
		}

		var power = nextPosition.cpy().sub(owner.getPosition()).scl(5);
		power.add((float)(Math.random() * 4) - 2, (float)(Math.random() * 4) - 2);
		owner.usePower(power, owner.stats.speed);
	}

	public void setTarget(float x, float y) {
		setTarget(new BotTarget.SimpleBotTarget(x, y));
	}

	public void setTarget(BotTarget target) {
		this.targeter.setTarget(target);
		this.target = target;
		this.isFinished = false;
	}

	public void setWaypoints(String[] waypoints) {
		this.waypoints = waypoints;
	}

	public void onCompleted(Runnable callback) {
		this.completionCallback = callback;
	}

	public void onFailed(Runnable callback) {
		this.failureCallback = callback;
	}
}