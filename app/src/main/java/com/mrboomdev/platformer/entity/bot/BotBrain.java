package com.mrboomdev.platformer.entity.bot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.EntityManager;
import com.mrboomdev.platformer.entity.character.CharacterBrain;
import com.mrboomdev.platformer.entity.character.CharacterEntity;

public class BotBrain extends CharacterBrain {
	private EntityManager entityManager;
	private CharacterEntity targetCharacter = null;
	private float attackReloadProgress, attackReloadDuration;
	private float shootReloadProgress, shootReloadDuration;
	private float dashReloadProgress, dashReloadDuration;
	
	public BotBrain(EntityManager entityManager) {
		this.entityManager = entityManager;
		attackReloadDuration = (float)(Math.random() * 1);
		shootReloadDuration = (float)(Math.random() * .4f);
		dashReloadDuration = (float)(Math.random() * 1);
	}
	
	@Override
	public void update() {
		setTargetEntity();
		if(targetCharacter != null) {
			float distance = targetCharacter.body.getPosition().dst(entity.body.getPosition());
			if(distance > 12) {
				explore();
			} else {
				if(distance < 1.9f) {
					if(attackReloadProgress > attackReloadDuration) {
						entity.attack(targetCharacter.body.getPosition()
							.sub(entity.body.getPosition()).scl(25));
						attackReloadProgress = 0;
						attackReloadDuration = (float)(Math.random() * 1);
					} else {
						attackReloadProgress += Gdx.graphics.getDeltaTime();
					}
				} else {
					if(shootReloadProgress > shootReloadDuration && false) {
						entity.shoot(Vector2.Zero);
						shootReloadProgress = 0;
						shootReloadDuration = (float)(Math.random() * .4f);
					} else {
						shootReloadProgress += Gdx.graphics.getDeltaTime();
					}
					if(dashReloadProgress > dashReloadDuration) {
						entity.dash();
						dashReloadProgress = 0;
						dashReloadDuration = (float)(Math.random() * 1);
					} else {
						dashReloadProgress += Gdx.graphics.getDeltaTime();
					}
				}
				entity.usePower(targetCharacter.body.getPosition()
					.sub(entity.body.getPosition()).scl(25),
					entity.config.stats.speed, true);
			}
		} else {
			explore();
		}
	}
	
	private void explore() {
		entity.usePower(new Vector2(
			(float)(Math.random() * 100) - 50,
			(float)(Math.random() * 100) - 50
		), entity.config.stats.speed, false);
	}
	
	private void setTargetEntity() {
		Array<CharacterEntity> characters = entityManager.getAllCharacters();
		for(CharacterEntity character : characters) {
			if(character == entity || character.isDead) continue;
			if(targetCharacter == null) targetCharacter = character;
			float distance = entity.body.getPosition().dst(character.body.getPosition());
			if(distance < entity.body.getPosition().dst(targetCharacter.body.getPosition())) {
				targetCharacter = character;
			}
		}
		if(targetCharacter == null) return;
		if(characters.isEmpty() || targetCharacter.isDead) targetCharacter = null;
	}
}