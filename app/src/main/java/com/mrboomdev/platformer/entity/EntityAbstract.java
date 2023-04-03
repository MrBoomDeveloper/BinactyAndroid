package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.entity.bot.BotTarget;
import com.mrboomdev.platformer.util.Direction;
import com.squareup.moshi.Json;

public abstract class EntityAbstract implements BotTarget {
	@Json(ignore = true) public World world;
	@Json(ignore = true) public Vector2 wasPower = new Vector2();
	@Json(ignore = true) public Body body;
	@Json(ignore = true) public boolean isDestroyed, isDead;
	
	public abstract void draw(SpriteBatch batch);
	
	public void die() {
		if(isDead) return;
		isDead = true;
	}
	
	public void destroy() {
		if(isDestroyed) return;
		world.destroyBody(body);
		isDestroyed = true;
	}
	
	public float getSpeed(Vector2 power) {
		return Math.max(
			Math.abs(power.x),
			Math.abs(power.y)
		);
	}
	
	public void usePower(Vector2 power, float speed, boolean isBot) {
		if(isDead) return;
		body.setLinearVelocity(isBot
			? power.limit(5).scl(speed).add(getRandomVector2(5))
			:  power.limit(5).scl(speed));
		if(!power.isZero()) wasPower = power.cpy();
	}
	
	private Vector2 getRandomVector2(float range) {
		return new Vector2(
			(float)(Math.random() * range) - (range / 2),
			(float)(Math.random() * range) - (range / 2)
		);
	}
	
	public Direction getDirection() {
		return new Direction(wasPower.x);
	}
	
	@Override
	public Vector2 getPosition() {
		return body.getPosition();
	}
}