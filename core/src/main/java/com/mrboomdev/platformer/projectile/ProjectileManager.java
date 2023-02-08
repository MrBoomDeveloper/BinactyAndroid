package com.mrboomdev.platformer.projectile;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.projectile.ProjectileBullet;
import com.mrboomdev.platformer.projectile.ProjectileBullet.ProjectileStats;

public class ProjectileManager {
    private World world;
	private int activeBullets;
	private ProjectileStats stats;
	private Array<ProjectileBullet> bullets = new Array<>();
	public Entity owner;
	
	public ProjectileManager(World world) {
		this.world = world;
	}
    
    public ProjectileManager setOwner(Entity owner) {
		this.owner = owner;
		return this;
    }
	
	public ProjectileManager setBulletConfig(ProjectileStats stats) {
		this.stats = stats;
		return this;
	}
	
	public ProjectileManager setAttackConfig() {
		return this;
	}
    
    public void shoot(Vector2 power) {
        bullets.add(new ProjectileBullet(world, owner, stats, power));
    }
	
	public void attack() {
		
	}
    
    public void create() {
        
    }
	
	public void render(SpriteBatch batch) {
		for(ProjectileBullet bullet : bullets) {
			bullet.draw(batch);
		}
	}
	
	public void clearTrash() {
		 for(ProjectileBullet bullet : bullets) {
			if(bullet.isDied) {
				bullet.destroy();
				bullets.removeValue(bullet, true);
			}
		}
	}
}