package com.mrboomdev.platformer.projectile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.EntityAbstract;
import com.mrboomdev.platformer.projectile.ProjectileAttack.AttackStats;
import com.mrboomdev.platformer.projectile.ProjectileBullet.ProjectileStats;

public class ProjectileManager {
    private World world;
	private int activeBullets;
	private ProjectileStats stats;
	private AttackStats attackStats;
	private Array<ProjectileBullet> bullets = new Array<>();
	private Array<ProjectileAttack> attacks = new Array<>();
	public float reloadProgress, delayProgress, attackDelayProgress;
	public EntityAbstract owner;
	
	public ProjectileManager(World world, EntityAbstract owner) {
		this.world = world;
		this.owner = owner;
    }
	
	public ProjectileManager setBulletConfig(ProjectileStats stats) {
		this.stats = stats;
		return this;
	}
	
	public ProjectileManager setAttackConfig(AttackStats attackStats) {
		this.attackStats = attackStats;
		return this;
	}
    
    public void shoot(Vector2 power) {
		if(delayProgress < stats.delay) return;
        bullets.add(new ProjectileBullet(world, owner, stats, power));
		delayProgress = 0;
    }
	
	public void attack() {
		if(attackDelayProgress < 0.5f) return;
		attacks.add(new ProjectileAttack(world, owner, attackStats));
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
			if(attack.isEnded) {
				attack.destroy();
				attacks.removeValue(attack, true);
			}
		}
	}
}