package com.mrboomdev.platformer.entity.bot.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.bot.BotTarget;
import com.mrboomdev.platformer.entity.character.CharacterBrain;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.path.PathGraph;
import com.mrboomdev.platformer.environment.path.PathPoint;
import com.mrboomdev.platformer.game.GameHolder;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AiTargeter {
	public float exploreTimeoutProgress;
	public BotTarget ignoredTarget;
	private float visionDistance = 8;
	private long ignoredStartedTime;
	private final CharacterBrain brain;
	private final GameHolder game = GameHolder.getInstance();
	private PathGraph graph;
	private List<BotTarget> targets;
	private BotTarget target, currentTarget;
	private boolean isExplorationEnabled;
	private PathPoint rememberedPosition;

	public AiTargeter(CharacterBrain brain) {
		this.brain = brain;
	}

	public void setGraph(PathGraph graph) {
		this.graph = graph;
	}

	public void update() {
		if(isExplorationEnabled) {
			exploreTimeoutProgress -= Gdx.graphics.getDeltaTime();
		}

		if(ignoredTarget != null && System.currentTimeMillis() > ignoredStartedTime + 5000) {
			ignoredTarget = null;
		}
	}

	@Nullable
	public BotTarget getTarget(BotTargetSelector selector) {
		//TODO: For a future multitargeting
		/*if(targets == null) targets = new ArrayList<>();
		targets.clear();

		for(var target : game.environment.entities.characters) {
			if(selector.isTarget(target)) targets.add(target);
		}

		if(targets.isEmpty()) return null;*/

		//FIXME: A temporary solution was to check only the main player during the demo stage of the project
		var me = brain.getEntity().getPosition();
		if(!isMainPlayerIgnored(me, target.getPosition())) {
			this.exploreTimeoutProgress = 0;
			this.rememberedPosition = null;
			this.currentTarget = target;
			return target;
		}

		//TODO: Implement multitargeting
		/*Collections.sort(targets, (previous, next) -> {
			var distancePrevious = previous.getPosition().dst(me);
			var distanceNext = next.getPosition().dst(me);
			return distancePrevious - distanceNext;
		});*/

		if(currentTarget instanceof CharacterEntity && rememberedPosition == null && currentTarget != ignoredTarget) {
			this.rememberedPosition = new PathPoint(currentTarget.getPosition().cpy());
			return rememberedPosition;
		}

		if(rememberedPosition != null) return rememberedPosition;

		if(currentTarget == null && isExplorationEnabled && exploreTimeoutProgress <= 0) {
			var randomPoint = graph.points.random();

			this.exploreTimeoutProgress = Math.min(randomPoint.position.dst(me) * 1.2f, 10);
			this.currentTarget = randomPoint;

			return randomPoint;
		}

		boolean didRememberedTarget = ((currentTarget != null) && (currentTarget instanceof PathPoint));
		return didRememberedTarget ? currentTarget : null;
	}

	@Nullable
	public Vector2 getPathTo(BotTarget target) {
		if(target == null) {
			this.currentTarget = null;
			return null;
		}

		var myPosition = brain.getEntity().getPosition();
		var myPoint = graph.findNearest(myPosition);
		var targetPoint = graph.findNearest(target.getPosition());
		var path = graph.findPath(myPoint, targetPoint);

		if(path.getCount() > 1) {
			return path.get(1).position;
		}

		if(myPoint.equals(targetPoint)) {
			this.rememberedPosition = null;
			this.currentTarget = null;
			return null;
		}

		return target.getPosition();
	}

	private boolean isMainPlayerIgnored(Vector2 myPosition, Vector2 targetPosition) {
		return game.settings.enableEditor
			|| game.settings.mainPlayer == ignoredTarget
			|| game.settings.mainPlayer.isDead
			|| myPosition.dst(targetPosition) > this.visionDistance;
	}

	public void setTarget(BotTarget target) {
		this.target = target;
	}
	
	public void setIgnored(BotTarget target) {
		this.ignoredTarget = target;
		this.ignoredStartedTime = System.currentTimeMillis();
	}

	public void setExplorationEnabled(boolean isEnabled) {
		this.isExplorationEnabled = isEnabled;
	}

	public void setVisionDistance(float distance) {
		this.visionDistance = distance;
	}

	public interface BotTargetSelector {
		boolean isTarget(BotTarget target);
	}
}