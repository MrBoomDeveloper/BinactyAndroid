package com.mrboomdev.platformer.projectile;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.EntityAbstract;
import com.mrboomdev.platformer.game.GameHolder;

public class ProjectileAttack {
	private final Sprite sprite;
	public final Body body;
	private final Animation<TextureRegion> animation;
	private float animationProgress;
	private final World world;
	public EntityAbstract owner;
	public boolean isEnded, isDead;
	public AttackStats stats;
	public Vector2 power;
	
	public ProjectileAttack(@NonNull World world, @NonNull EntityAbstract owner, AttackStats stats, @NonNull Vector2 power) {
		this.world = world;
		this.owner = owner;
		this.stats = stats;
		this.power = power;
		
		AssetManager assets = GameHolder.getInstance().assets;
		Texture texture = assets.get("world/effects/attack.png", Texture.class);
		sprite = new Sprite(texture);
		
		TextureRegion[] animationFrames = new TextureRegion[]{
			new TextureRegion(texture, 0, 0, 8, 8),
			new TextureRegion(texture, 8, 0, 8, 8),
			new TextureRegion(texture, 16, 0, 8, 8),
			new TextureRegion(texture, 24, 0, 8, 8),
			new TextureRegion(texture, 32, 0, 8, 8),
			new TextureRegion(texture, 40, 0, 8, 8),
			new TextureRegion(texture, 48, 0, 8, 8),
			new TextureRegion(texture, 56, 0, 8, 8)
		};
		animation = new Animation<TextureRegion>(.04f, new Array<TextureRegion>(animationFrames), Animation.PlayMode.REVERSED);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.KinematicBody;
		bodyDef.position.set(owner.body.getPosition().add(power.limit(1.2f)));
		body = world.createBody(bodyDef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(1, .5f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = Entity.ATTACK;
		fixtureDef.filter.maskBits = Entity.CHARACTER;
		
		body.createFixture(fixtureDef);
		shape.dispose();
		body.setUserData(this);
	}
	
	public void draw(SpriteBatch batch) {
		if(isEnded) return;
		if(animationProgress > animation.getAnimationDuration()) {
			isEnded = true;
			isDead = true;
		}
		
		animationProgress += Gdx.graphics.getDeltaTime();
		sprite.set(new Sprite(animation.getKeyFrame(animationProgress)));
		sprite.setSize(-.8f, .8f);
		sprite.setCenter(body.getPosition().x, body.getPosition().y);
		sprite.draw(batch);
	}
	
	public void destroy() {
		world.destroyBody(body);
	}
	
	public static class AttackStats {
		public int damage = 25;
		public float delay = .1f;
		public float duration = 1;
		
		public AttackStats setDamage(int damage) {
			this.damage = damage;
			return this;
		}
		
		public AttackStats setDelay(float delay) {
			this.delay = delay;
			return this;
		}
		
		public AttackStats setDuration(float duration) {
			this.duration = duration;
			return this;
		}
	}
}