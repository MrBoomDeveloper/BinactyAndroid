package com.mrboomdev.platformer.entity.bot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.bot.ai.AiStuckChecker;
import com.mrboomdev.platformer.entity.bot.ai.AiTargeter;
import com.mrboomdev.platformer.entity.character.CharacterBrain;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.path.presets.MapScanner;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;

public class BotBrain extends CharacterBrain {
	public AiStuckChecker stuckChecker = new AiStuckChecker(this);
	protected Responder responder;
	protected int refreshRate;
	private BotTarget lastTargetWarned;
	private final AiTargeter targeter = new AiTargeter(this);
	private final GameHolder game = GameHolder.getInstance();
	private float changeRandomPowerDelay;
	private Sound playerDetectedSound;
	private final Vector2 randomPower = new Vector2();
	private long playerLastDetected, mapLastScanned;

	@Override
	public void start() {
		this.targeter.setVisionDistance(8);
		this.targeter.setTarget(game.settings.mainPlayer);
		this.targeter.setExplorationEnabled(true);

		this.playerDetectedSound = game.assets.get("audio/sounds/player_detected.wav");
		this.scanMap();
	}
	
	public void scanMap() {
		var tiles = game.environment.map.tilesMap.values();
		var graph = MapScanner.getGraphByWaypoints(tiles, responder.getWaypoints());

		this.targeter.setGraph(graph);
	}
	
	@Override
	public void update() {
		var owner = this.getEntity();
		var ownerPosition = owner.getPosition();
		long currentTime = System.currentTimeMillis();

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

		if((refreshRate != 0) && (currentTime > mapLastScanned + refreshRate * 1000f)) {
			this.mapLastScanned = currentTime;
			this.scanMap();
		}

		this.targeter.update();

		var selectedTarget = targeter.getTarget(object -> {
			if(object == game.settings.mainPlayer && object != targeter.ignoredTarget) {
				targeter.setVisionDistance(12);
				return true;
			}

			targeter.setVisionDistance(8);
			return false;
		});

		boolean isEnemyCharacter = selectedTarget instanceof CharacterEntity;

		if(isEnemyCharacter) {
			var enemyPosition = selectedTarget.getPosition().cpy();

			if(ownerPosition.dst(enemyPosition) < 2) {
				owner.attack(enemyPosition.sub(ownerPosition));
			}
		}

		var path = targeter.getPath(selectedTarget);
		var nextPosition = targeter.getPower(path, selectedTarget);

		if(nextPosition == null) {
			owner.usePower(Vector2.Zero, 0);
			stuckChecker.reset();
			this.lastTargetWarned = selectedTarget;
			return;
		}

		stuckChecker.setDestination(nextPosition, path.getTotalCost() * 1.5f);
		stuckChecker.update();

		if(stuckChecker.isStuck()) {
			System.out.println("FUCK! I'M STUCK!");

			stuckChecker.reset();
			targeter.setIgnored(selectedTarget);
			targeter.exploreTimeoutProgress = 0;
		}

		var speed = owner.stats.speed * (isEnemyCharacter ? 1.6f : 1);

		if(isEnemyCharacter && (owner.stats.health < (owner.stats.maxHealth / 3))) {
			owner.usePower(nextPosition.cpy().sub(owner.getPosition()).scl(-5).add(randomPower), speed);
			this.lastTargetWarned = selectedTarget;
			return;
		}

		if(isEnemyCharacter && selectedTarget != lastTargetWarned && System.currentTimeMillis() > playerLastDetected + 10000) {
			playerLastDetected = System.currentTimeMillis();
			playerDetectedSound.play(AudioUtil.soundVolume / 3);
		}

		owner.usePower(nextPosition.cpy().sub(owner.getPosition()).scl(5).add(randomPower), speed);
		this.lastTargetWarned = selectedTarget;
	}
	
	public interface Responder {
		String[] getWaypoints();
	}
}