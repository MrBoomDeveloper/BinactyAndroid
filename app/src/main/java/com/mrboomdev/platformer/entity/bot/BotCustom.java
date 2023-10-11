package com.mrboomdev.platformer.entity.bot;

import androidx.annotation.NonNull;

import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.bot.ai.AiStuckChecker;
import com.mrboomdev.platformer.entity.bot.ai.AiTargeter;
import com.mrboomdev.platformer.entity.character.CharacterBrain;
import com.mrboomdev.platformer.environment.path.PathGraph;
import com.mrboomdev.platformer.environment.path.presets.MapScanner;
import com.mrboomdev.platformer.game.GameHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BotCustom extends CharacterBrain {
	public final AiTargeter targeter = new AiTargeter(this);
	public final AiStuckChecker stuckChecker = new AiStuckChecker(this);
	private PathGraph graph;
	private final List<BotTarget> potentialTargets = new ArrayList<>();
	private final GameHolder game = GameHolder.getInstance();
	private Runnable updateListener;
	private String[] waypoints;
	private final float speed = 1;
	private final float visionDistance = 8;

	public void setUpdateListener(Runnable listener) {
		this.updateListener = listener;
	}

	private void scanMap() {
		var tiles = game.environment.map.tilesMap.values();
		graph = MapScanner.getGraphByWaypoints(tiles, waypoints);
		targeter.setGraph(graph);
	}

	@Override
	public void update() {
		if(graph == null && waypoints != null) scanMap();
		if(updateListener != null) updateListener.run();
		if(graph == null) return;

		this.targeter.update();
		this.stuckChecker.update();
	}

	public void escape() {
		stuckChecker.reset();
		targeter.exploreTimeoutProgress = 0;
	}

	public void goTo(@NonNull BotTarget target, float speed) {
		var path = targeter.getPath(target);
		var power = Objects.requireNonNullElse(targeter.getPower(path, target), Vector2.Zero);

		float time = (path != null) ? (path.getTotalCost() * speed * 1.5f) : 2;
		stuckChecker.setDestination(target.getPosition(), time);

		getEntity().usePower(power, speed);
	}

	public BotTarget getRandomTarget() {
		return game.environment.map.tilesMap.values().toArray().random();
	}

	private void update2() {
		//var owner = this.getEntity();
		//var ownerPosition = owner.getPosition();

		//var selectedTarget = targeter.getTarget(object -> (object == game.settings.mainPlayer));
		//boolean isEnemyCharacter = selectedTarget instanceof CharacterEntity;

		/*var nextPosition = targeter.getPathTo(selectedTarget);
		if(nextPosition == null) {
			owner.usePower(Vector2.Zero, 0);
			stuckChecker.reset();
			return;
		}*/

		//stuckChecker.setDestination(nextPosition);
		//stuckChecker.update(ownerPosition);

		//var speed = owner.stats.speed * (isEnemyCharacter ? 1.5f : 1);
		//var randomPower = new Vector2((float)(Math.random() * 4) - 2, (float)(Math.random() * 4) - 2);

		/*if(isEnemyCharacter && (owner.stats.health < (owner.stats.maxHealth / 3))) {
			owner.usePower(nextPosition.cpy().sub(owner.getPosition()).scl(-5).add(randomPower), speed);
			return;
		}*/

		//owner.usePower(nextPosition.cpy().sub(owner.getPosition()).scl(5).add(randomPower), speed);
	}

	public List<BotTarget> getTargets() {
		return potentialTargets;
	}

	public PathGraph getGraph() {
		return graph;
	}

	public float getDistance(@NonNull BotTarget target) {
		return getEntity().getPosition().dst(target.getPosition());
	}

	public void setTarget(BotTarget target) {
		targeter.setTarget(target);
	}

	public boolean isStuck() {
		return stuckChecker.isStuck();
	}

	public void setExploring(boolean isExploring) {
		this.targeter.setExplorationEnabled(isExploring);
	}

	public void setVisionDistance(float distance) {
		targeter.setVisionDistance(distance);
	}

	public void setWaypoints(String[] waypoints) {
		this.waypoints = waypoints;
	}

	public void addTarget(BotTarget target) {
		potentialTargets.add(target);
	}
}