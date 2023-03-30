package com.mrboomdev.platformer.entity.bot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.character.CharacterBrain;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.path.PathGraph;
import com.mrboomdev.platformer.environment.path.PathPoint;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.AudioUtil;

public class BotBrain extends CharacterBrain {
	public GraphPath<PathPoint> path;
	public PathGraph graph;
	private EntityManager entityManager;
	private float attackReloadProgress, attackReloadDuration;
	private float dashReloadProgress, dashReloadDuration;
	private GameHolder game = GameHolder.getInstance();
	private float exploreTimeoutProgress;
	private BotTarget target;
	private Sound playerDetectedSound;
	private long playerLastDetected;
	
	public BotBrain(EntityManager entityManager) {
		this.entityManager = entityManager;
		attackReloadDuration = (float)(Math.random() * 1);
		dashReloadDuration = (float)(Math.random() * 1);
		playerDetectedSound = game.assets.get("audio/sounds/player_detected.wav");
		scanMap();
	}
	
	public void scanMap() {
		this.graph = new PathGraph();
		var points = new Array<PathPoint>();
		for(var tile : game.environment.map.tilesMap.values()) {
			if(!tile.name.equals("triggerAi") && !tile.name.equals("triggerSpawn")) continue;
			var point = new PathPoint(tile.getPosition(false));
			this.graph.addPoint(point);
			points.add(point);
		}
		
		for(int i = 0; i < points.size; i++) {
			for(int a = 0; a < points.size; a++) {
				if(points.get(i).position.dst(points.get(a).position) > 2.5f) continue;
				this.graph.connectPoints(points.get(i), points.get(a));
			}
		}
	}
	
	@Override
	public void update() {
		var myPoint = graph.findNearest(entity.getPosition());
		var targetPoint = graph.findNearest(game.settings.mainPlayer.getPosition());
		
		if(myPoint.position.dst(targetPoint.position) > 8 || game.settings.enableEditor) {
			if(exploreTimeoutProgress <= 0) {
				target = graph.points.random();
				exploreTimeoutProgress = Math.min(target.getPosition().dst(entity.getPosition()) * 1.2f, 10);
			}
			exploreTimeoutProgress -= Gdx.graphics.getDeltaTime();
			targetPoint = graph.findNearest(target.getPosition());
			path = graph.findPath(myPoint, targetPoint);
			
			if(myPoint != targetPoint) {
				goByPath(entity.stats.speed, false);
			} else {
				entity.usePower(Vector2.Zero, 0, false);
			}
			return;
		}
		
		exploreTimeoutProgress = 0;
		path = graph.findPath(myPoint, targetPoint);
		goByPath(entity.stats.speed * 1.5f, true);
		target = game.settings.mainPlayer;
	}
	
	private void goByPath(float speed, boolean toEnemy) {
		boolean shouldGoAway = false;
		
		if(toEnemy) {
			if(entity.getPosition().dst(target.getPosition()) < 2) entity.attack(Vector2.Zero);
			if(entity.stats.health < entity.stats.maxHealth / 2) shouldGoAway = true;
			if(entity.stats.stamina > entity.stats.maxStamina / 2) entity.dash();
		}
		
		if(toEnemy && target != game.settings.mainPlayer && !shouldGoAway && System.currentTimeMillis() > playerLastDetected + 5000) {
			playerLastDetected = System.currentTimeMillis();
			playerDetectedSound.play(AudioUtil.soundVolume / 3);
		}
		
		if(path.getCount() > 1) {
			entity.usePower(path.get(1).position.sub(entity.getPosition()).scl(25).scl(shouldGoAway ? -1.7f : 1), speed, true);
		} else if(target instanceof CharacterEntity) {
			entity.usePower(target.getPosition().sub(entity.getPosition()).scl(shouldGoAway ? -1.7f : 1), speed, true);
		}
	}
}