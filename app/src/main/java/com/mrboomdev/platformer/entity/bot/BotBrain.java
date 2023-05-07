package com.mrboomdev.platformer.entity.bot;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.bot.ai.*;
import com.mrboomdev.platformer.entity.character.CharacterBrain;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.path.PathGraph;
import com.mrboomdev.platformer.environment.path.PathPoint;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.AudioUtil;
import com.mrboomdev.platformer.util.io.LogUtil;

public class BotBrain extends CharacterBrain {
	public GraphPath<PathPoint> path;
	public PathGraph graph;
	public BotTarget target;
	public AiStuckChecker stuckChecker;
	protected Responder responder;
	protected int refreshRate;
	private AiTargeter targeter;
	private EntityManager entityManager;
	private float attackReloadProgress, attackReloadDuration;
	private float dashReloadProgress, dashReloadDuration;
	private GameHolder game = GameHolder.getInstance();
	private Sound playerDetectedSound;
	private long playerLastDetected, mapLastScanned;
	
	public BotBrain start(EntityManager entityManager) {
		this.entityManager = entityManager;
		this.stuckChecker = new AiStuckChecker();
		this.targeter = new AiTargeter(this);
		attackReloadDuration = (float)(Math.random() * 1);
		dashReloadDuration = (float)(Math.random() * 1);
		playerDetectedSound = game.assets.get("audio/sounds/player_detected.wav");
		scanMap();
		return this;
	}
	
	public void scanMap() {
		var startedScanningMapMs = System.currentTimeMillis();
		
		this.graph = new PathGraph();
		var points = new Array<PathPoint>();
		for(var tile : game.environment.map.tilesMap.values()) {
			boolean tileIsOk = false;
			for(var waypoint : responder.getWaypoints()) {
				if(waypoint.equals(tile.name)) tileIsOk = true;
			}
			if(!tileIsOk) continue;
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
		
		LogUtil.debug(LogUtil.Tag.BOT, "Map scanned for: " + (System.currentTimeMillis() - startedScanningMapMs) + "ms");
	}
	
	@Override
	public void update() {
		if((refreshRate != 0) && (System.currentTimeMillis() > mapLastScanned + refreshRate * 1000)) {
			mapLastScanned = System.currentTimeMillis();
			scanMap();
		}
		
		targeter.update();
	}
	
	public void goByPath(float speed, boolean toEnemy) {
		if(entity == null || target == null) return;
		boolean shouldGoAway = false;
		
		if(toEnemy) {
			if(entity.getPosition().dst(target.getPosition()) < 2) entity.attack(Vector2.Zero);
			if(entity.stats.health < entity.stats.maxHealth / 3) shouldGoAway = true;
			if(entity.stats.stamina > entity.stats.maxStamina / 3) entity.dash();
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
		
		if(stuckChecker.isStuck(entity.getPosition())) {
			stuckChecker.reset();
			targeter.setIgnored(target);
			targeter.exploreTimeoutProgress = 0;
		}
	}
	
	public interface Responder {
		public String[] getWaypoints();
		public BotTarget getTarget();
	}
}