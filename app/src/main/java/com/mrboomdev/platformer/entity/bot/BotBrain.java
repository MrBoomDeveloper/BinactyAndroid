package com.mrboomdev.platformer.entity.bot;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.bot.ai.AiState;
import com.mrboomdev.platformer.entity.bot.ai.AiStuckChecker;
import com.mrboomdev.platformer.entity.bot.ai.AiTargeter;
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
	public AiState state;
	public AiStuckChecker stuckChecker;
	protected Responder responder;
	protected int refreshRate;
	private AiTargeter targeter;
	private float attackReloadProgress;
	private float dashReloadProgress;
	private final GameHolder game = GameHolder.getInstance();
	private Sound playerDetectedSound;
	private long playerLastDetected, mapLastScanned;

	@Override
	public void start() {
		this.stuckChecker = new AiStuckChecker();
		this.targeter = new AiTargeter(this);
		float attackReloadDuration = (float) (Math.random() * 1);
		float dashReloadDuration = (float) (Math.random() * 1);
		playerDetectedSound = game.assets.get("audio/sounds/player_detected.wav");
		this.scanMap();
	}
	
	public void scanMap() {
		var startedScanningMapMs = System.currentTimeMillis();

		this.graph = new PathGraph();
		var points = new Array<PathPoint>();
		for(var tile : game.environment.map.tilesMap.values()) {
			if(!new Array<>(responder.getWaypoints()).contains(tile.name, false)) continue;

			var point = new PathPoint(tile.getCachedPosition());
			this.graph.addPoint(point);
			points.add(point);
		}

		for(int i = 0; i < points.size; i++) {
			for(int a = 0; a < points.size; a++) {
				if(points.get(i).position.dst(points.get(a).position) > 2.5f) continue;
				this.graph.connectPoints(points.get(i), points.get(a));
			}
		}

		targeter.setGraph(graph);
		LogUtil.debug(LogUtil.Tag.BOT, "Map scanned for: " + (System.currentTimeMillis() - startedScanningMapMs) + "ms");
	}
	
	@Override
	public void update() {
		long currentTime = System.currentTimeMillis();
		if((refreshRate != 0) && (currentTime > mapLastScanned + refreshRate * 1000f)) {
			mapLastScanned = currentTime;
			scanMap();
		}

		if(graph != null) {
			targeter.update();
		}
	}
	
	public void goByPath(float speed, boolean toEnemy) {
		var entity = getEntity();

		if(entity == null || target == null) return;
		boolean shouldGoAway = false;
		var myPosition = entity.getPosition();
		var targetPosition = target.getPosition();
		
		if(toEnemy) {
			if(myPosition.dst(targetPosition) < 2) {
				entity.attack(targetPosition.cpy().sub(myPosition).scl(2));
			}

			if(entity.stats.health < entity.stats.maxHealth / 3) shouldGoAway = true;
			//if(entity.stats.stamina > entity.stats.maxStamina / 3) entity.dash();
		}
		
		if(toEnemy && target != game.settings.mainPlayer && !shouldGoAway && System.currentTimeMillis() > playerLastDetected + 5000) {
			playerLastDetected = System.currentTimeMillis();
			playerDetectedSound.play(AudioUtil.soundVolume / 3);
		}
		
		if(path.getCount() > 1) {
			entity.usePower(path.get(1).position.cpy().sub(myPosition).scl(25).scl(shouldGoAway ? -1.7f : 1), speed, true);
		} else if(target instanceof CharacterEntity) {
			entity.usePower(myPosition.cpy().sub(myPosition).scl(shouldGoAway ? -1.7f : 1), speed, true);
		}
		
		if(stuckChecker.isStuck(myPosition) && myPosition.dst(targetPosition) > 1.25f) {
			stuckChecker.reset();
			targeter.setIgnored(target);
			targeter.exploreTimeoutProgress = 0;
		}
	}
	
	public interface Responder {
		String[] getWaypoints();
	}
}