package com.mrboomdev.platformer.entity.bot.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import com.mrboomdev.platformer.entity.bot.BotBrain;
import com.mrboomdev.platformer.entity.bot.BotTarget;
import com.mrboomdev.platformer.game.GameHolder;

public class AiTargeter {
	public float exploreTimeoutProgress;
	public BotTarget ignoredTarget;
	private float visionDistance = 8;
	private long ignoredStartedTime;
	private final BotBrain brain;
	private final GameHolder game = GameHolder.getInstance();

	public AiTargeter(BotBrain brain) {
		this.brain = brain;
	}
	
	public void update() {
		var myPoint = brain.graph.findNearest(brain.entity.getPosition());
		var targetPoint = brain.graph.findNearest(game.settings.mainPlayer.getPosition());
		
		if(ignoredTarget != null && System.currentTimeMillis() > ignoredStartedTime + 5000) {
			ignoredTarget = null;
		}
		
		if(myPoint.position.dst(targetPoint.position) > visionDistance || game.settings.enableEditor || game.settings.mainPlayer == ignoredTarget || game.settings.mainPlayer.isDead) {
			visionDistance = 8;
			if(exploreTimeoutProgress <= 0) {
				brain.target = brain.graph.points.random();
				exploreTimeoutProgress = Math.min(brain.target.getPosition().dst(brain.entity.getPosition()) * 1.2f, 10);
				brain.stuckChecker.setDestination(brain.target.getPosition().cpy(), brain.target.getPosition().dst(brain.entity.getPosition()) * 1.2f);
			}
			exploreTimeoutProgress -= Gdx.graphics.getDeltaTime();
			targetPoint = brain.graph.findNearest(brain.target.getPosition());
			brain.path = brain.graph.findPath(myPoint, targetPoint);
			
			if(myPoint != targetPoint) {
				brain.goByPath(brain.entity.stats.speed, false);
			} else {
				brain.entity.usePower(Vector2.Zero, 0, false);
			}
			return;
		}
		
		visionDistance = 12;
		exploreTimeoutProgress = 0;
		brain.path = brain.graph.findPath(myPoint, targetPoint);
		brain.goByPath(brain.entity.stats.speed * 1.5f, true);
		brain.target = game.settings.mainPlayer;
	}
	
	public void setIgnored(BotTarget target) {
		this.ignoredTarget = target;
		this.ignoredStartedTime = System.currentTimeMillis();
	}
}