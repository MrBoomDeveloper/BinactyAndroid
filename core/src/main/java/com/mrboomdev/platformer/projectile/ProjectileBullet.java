package com.mrboomdev.platformer.projectile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
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
import com.mrboomdev.platformer.entity.EntityAbstract;
import com.mrboomdev.platformer.util.Direction;

public class ProjectileBullet {
    private World world;
    private Body body;
    private Sprite sprite;
    private Vector2 power;
    public ProjectileStats stats;
    public EntityAbstract owner;
    public boolean isDied;

    public ProjectileBullet(World world, EntityAbstract owner, ProjectileStats stats, Vector2 power) {
        this.world = world;
        this.owner = owner;
        this.power = power;
        this.stats = stats;
		AssetManager assets = MainGame.getInstance().asset;
        sprite = new Sprite(assets.get("world/player/weapon/projectile/bullet_fire.png", Texture.class));

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
        body.setLinearVelocity(power.scl(100).limit(stats.speed).add(
			(float) (Math.random() * stats.random),
			(float) (Math.random() * stats.random)));
    }

    public void draw(SpriteBatch batch) {
        if (isDied) return;
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
        public int amount = 100, amountPerReload = 10;
        public float speed = 25;
        public float random = 0;
        public float delay = .1f;
        public float reloadTime = 1;

        public ProjectileStats setDamage(int damage) {
            this.damage = damage;
            return this;
        }

        public ProjectileStats setAmount(int amount, int amountPerReload) {
            this.amount = amount;
			this.amountPerReload = amountPerReload;
            return this;
        }

        public ProjectileStats setSpeed(float speed) {
            this.speed = speed;
            return this;
        }

        public ProjectileStats setRandom(float random) {
            this.random = random;
            return this;
        }
		
		public ProjectileStats setDelay(float delay) {
			this.delay = delay;
			return this;
		}
		
		public ProjectileStats setReloadTime(float reloadTime) {
			this.reloadTime = reloadTime;
			return this;
		}
    }
}
