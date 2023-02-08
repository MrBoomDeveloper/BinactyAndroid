package com.mrboomdev.platformer.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.util.Direction;

public class ProjectileBullet {
	private World world;
	private Body body;
	private Sprite sprite;
	private Vector2 power;
	public ProjectileStats stats;
	public Entity owner;
	public boolean isDied;
	
	public ProjectileBullet(World world, Entity owner, ProjectileStats stats, Vector2 power) {
		this.world = world;
		this.owner = owner;
		this.power = power;
		this.stats = stats.cpy();
		sprite = new Sprite(MainGame.getInstance().asset.get("world/player/weapon/projectile/bullet_fire.png", Texture.class));
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		Direction direction = new Direction(power.x);
		bodyDef.position.set(owner.body.getPosition().add(direction.isForward() ? 0.75f : -0.75f, 0));
		body = world.createBody(bodyDef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.125f, 0.125f);
		sprite.setSize(0.25f, 0.25f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		body.createFixture(fixtureDef);
		
		shape.dispose();
		body.setUserData(this);
	}
	
	public void draw(SpriteBatch batch) {
		if(isDied) return;
		body.setLinearVelocity(power.scl(100).limit(stats.speed));
		sprite.setCenter(body.getPosition().x, body.getPosition().y);
		sprite.draw(batch);
	}
	
	public void deactivate() {
		isDied = true;
	}
	
	public void destroy() {
		world.destroyBody(body);
	}
	
	public static class ProjectileStats {
		public int damage = 15;
		public float speed = 10;
		
		public ProjectileStats(int damage, float speed) {
			this.damage = damage;
			this.speed = speed;
		}
		
		public ProjectileStats cpy() {
			return new ProjectileStats(damage, speed);
		}
	}
}