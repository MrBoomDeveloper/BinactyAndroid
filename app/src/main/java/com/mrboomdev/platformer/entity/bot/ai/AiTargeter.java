package com.mrboomdev.platformer.entity.bot.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.bot.BotBrain;
import com.mrboomdev.platformer.entity.bot.BotTarget;
import com.mrboomdev.platformer.environment.path.PathGraph;
import com.mrboomdev.platformer.game.GameHolder;

public class AiTargeter {
	public float exploreTimeoutProgress;
	public BotTarget ignoredTarget;
	private float visionDistance = 8;
	private long ignoredStartedTime;
	private final BotBrain brain;
	private final GameHolder game = GameHolder.getInstance();
	private PathGraph graph;

	public AiTargeter(BotBrain brain) {
		this.brain = brain;
	}

	public void setGraph(PathGraph graph) {
		this.graph = graph;
	}
	
	public void update() {
		if(graph == null) return;

		var entity = brain.getEntity();
		var myPosition = entity.getPosition();
		var myPoint = graph.findNearest(myPosition);
		var targetPoint = graph.findNearest(game.settings.mainPlayer.getPosition());
		
		if(ignoredTarget != null && System.currentTimeMillis() > ignoredStartedTime + 5000) {
			ignoredTarget = null;
		}

		if(isMainPlayerIgnored(myPoint.position, targetPoint.position)) {
			this.visionDistance = 8;

			if(exploreTimeoutProgress <= 0) brain.target = graph.points.random();
			var targetPosition = brain.target.getPosition();

			if(exploreTimeoutProgress <= 0) {
				exploreTimeoutProgress = Math.min(targetPosition.dst(myPosition) * 1.2f, 10);
				brain.stuckChecker.reset();
			}

			this.exploreTimeoutProgress -= Gdx.graphics.getDeltaTime();
			targetPoint = graph.findNearest(targetPosition);
			brain.path = graph.findPath(myPoint, targetPoint);
			
			if(myPoint != targetPoint) {
				brain.goByPath(entity.stats.speed, false);
			} else {
				entity.usePower(Vector2.Zero, 0, false);
			}
			return;
		}
		
		this.visionDistance = 12;
		exploreTimeoutProgress = 0;
		brain.path = graph.findPath(myPoint, targetPoint);
		brain.stuckChecker.setDestination(targetPoint.getPosition().cpy());
		brain.goByPath(entity.stats.speed * 1.5f, true);
		brain.target = game.settings.mainPlayer;
	}

	public Vector2 getTarget() {
		if(graph == null) return Vector2.Zero;

		var entity = brain.getEntity();
		var myPosition = entity.getPosition();
		var myPoint = graph.findNearest(myPosition);
		var targetPoint = graph.findNearest(game.settings.mainPlayer.getPosition());

		if(ignoredTarget != null && System.currentTimeMillis() > ignoredStartedTime + 5000) {
			ignoredTarget = null;
		}

		if(isMainPlayerIgnored(myPoint.position, targetPoint.position)) {
			this.visionDistance = 8;

			if(exploreTimeoutProgress <= 0) brain.target = graph.points.random();
			var targetPosition = brain.target.getPosition();

			if(exploreTimeoutProgress <= 0) {
				exploreTimeoutProgress = Math.min(targetPosition.dst(myPosition) * 1.2f, 10);
				brain.stuckChecker.reset();
			}

			this.exploreTimeoutProgress -= Gdx.graphics.getDeltaTime();
			targetPoint = graph.findNearest(targetPosition);
			brain.path = graph.findPath(myPoint, targetPoint);

			if(myPoint != targetPoint) {
				brain.goByPath(entity.stats.speed, false);
			} else {
				entity.usePower(Vector2.Zero, 0, false);
			}
			return null;
		}

		this.visionDistance = 12;
		exploreTimeoutProgress = 0;
		brain.path = graph.findPath(myPoint, targetPoint);
		brain.stuckChecker.setDestination(targetPoint.getPosition().cpy());
		brain.goByPath(entity.stats.speed * 1.5f, true);
		brain.target = game.settings.mainPlayer;
		return null;
	}

	private boolean isMainPlayerIgnored(Vector2 myPosition, Vector2 targetPosition) {
		return game.settings.enableEditor
			|| game.settings.mainPlayer == ignoredTarget
			|| game.settings.mainPlayer.isDead
			|| myPosition.dst(targetPosition) > this.visionDistance;
	}
	
	public void setIgnored(BotTarget target) {
		this.ignoredTarget = target;
		this.ignoredStartedTime = System.currentTimeMillis();
	}
}