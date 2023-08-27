package com.mrboomdev.platformer.entity.bot;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.bot.ai.AiStuckChecker;
import com.mrboomdev.platformer.entity.bot.ai.AiTargeter;
import com.mrboomdev.platformer.entity.character.CharacterBrain;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.environment.path.presets.MapScanner;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.AudioUtil;

import java.util.Arrays;

public class BotBrain extends CharacterBrain {
	public AiStuckChecker stuckChecker = new AiStuckChecker();
	protected Responder responder;
	protected int refreshRate;
	private final AiTargeter targeter = new AiTargeter(this);
	private final GameHolder game = GameHolder.getInstance();
	private Sound playerDetectedSound;
	private final MapScanner mapScanner = new MapScanner();
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
		var graph = mapScanner.getGraph(game.environment.map.tilesMap.values(), tile -> {
			var stream = Arrays.stream(responder.getWaypoints());
			return stream.anyMatch(item -> tile.name.equals(item));
		});

		this.targeter.setGraph(graph);
	}
	
	@Override
	public void update() {
		var owner = this.getEntity();
		var ownerPosition = owner.getPosition();
		long currentTime = System.currentTimeMillis();

		if((refreshRate != 0) && (currentTime > mapLastScanned + refreshRate * 1000f)) {
			this.mapLastScanned = currentTime;
			this.scanMap();
		}

		this.targeter.update();

		var selectedTarget = targeter.getTarget(object -> {
			if(object == game.settings.mainPlayer) {
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

		var nextPosition = targeter.getPathTo(selectedTarget);
		if(nextPosition == null) {
			owner.usePower(Vector2.Zero, 0);
			stuckChecker.reset();
			return;
		}

		stuckChecker.setDestination(nextPosition);
		stuckChecker.update(ownerPosition);

		if(stuckChecker.isStuck()) {
			stuckChecker.reset();
			targeter.setIgnored(selectedTarget);
			targeter.exploreTimeoutProgress = 0;
		}

		var speed = owner.stats.speed * (isEnemyCharacter ? 1.5f : 1);
		var randomPower = new Vector2((float)(Math.random() * 4) - 2, (float)(Math.random() * 4) - 2);

		if(isEnemyCharacter && (owner.stats.health < (owner.stats.maxHealth / 3))) {
			owner.usePower(nextPosition.cpy().sub(owner.getPosition()).scl(-5).add(randomPower), speed);
			return;
		}

		if(isEnemyCharacter && System.currentTimeMillis() > playerLastDetected + 10000) {
			playerLastDetected = System.currentTimeMillis();
			playerDetectedSound.play(AudioUtil.soundVolume / 3);
		}

		owner.usePower(nextPosition.cpy().sub(owner.getPosition()).scl(5).add(randomPower), speed);
	}
	
	public interface Responder {
		String[] getWaypoints();
	}
}