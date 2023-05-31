package com.mrboomdev.platformer.projectile;

import android.annotation.SuppressLint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.character.CharacterEntity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.projectile.ProjectileAttack.AttackStats;
import com.mrboomdev.platformer.util.AudioUtil;

public class ProjectileManager {
    private final World world;
	private final GameHolder game = GameHolder.getInstance();
	private AttackStats attackStats;
	private final Array<ProjectileBullet> bullets = new Array<>();
	private final Array<ProjectileAttack> attacks = new Array<>();
	public float reloadProgress, delayProgress, attackDelayProgress;
	public CharacterEntity owner;
	
	public ProjectileManager(World world, CharacterEntity owner) {
		this.world = world;
		this.owner = owner;
    }
	
	public ProjectileManager setAttackConfig(AttackStats attackStats) {
		this.attackStats = attackStats;
		return this;
	}
    
    @SuppressLint("SuspiciousIndentation")
	public void shoot(Vector2 power) {
		if(delayProgress < .1f) return;
        bullets.add(new ProjectileBullet(world, owner, power));
		delayProgress = 0;
		AudioUtil.play3DSound(game.assets.get("audio/sounds/shot.wav"), .1f, 25, owner.getPosition());
    }
	
	public void attack(Vector2 power) {
		if(attackDelayProgress < 0.5f) return;
		attacks.add(new ProjectileAttack(world, owner, attackStats, power));
		attackDelayProgress = 0;
	}
	
	public void render(SpriteBatch batch) {
		float delta = Gdx.graphics.getDeltaTime();
		reloadProgress += delta;
		delayProgress += delta;
		attackDelayProgress += delta;
		for(ProjectileBullet bullet : bullets) {
			bullet.draw(batch);
		}
		for(ProjectileAttack attack : attacks) {
			attack.draw(batch);
		}
	}
	
	public void clearTrash() {
		 for(ProjectileBullet bullet : bullets) {
			if(bullet.isDied) {
				bullet.destroy();
				bullets.removeValue(bullet, true);
			}
		}
		for(ProjectileAttack attack : attacks) {
			if(attack.isDead && attack.isEnded) {
				attack.destroy();
				attacks.removeValue(attack, true);
			}
		}
	}
}