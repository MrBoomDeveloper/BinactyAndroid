package com.mrboomdev.platformer.projectile;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.EntityAbstract;

public class ProjectileBullet {
    public Body body;
    public ProjectileStats stats;
    public EntityAbstract owner;
	public Vector2 power;
    public boolean isDied;
	private Sprite sprite;
	private final World world;

    public ProjectileBullet(@NonNull World world, @NonNull EntityAbstract owner, ProjectileStats stats, @NonNull Vector2 power) {
        this.world = world;
        this.owner = owner;
        this.power = power;
        this.stats = new ProjectileStats();
        sprite = new Sprite(new Texture(Gdx.files.internal("world/effects/boom.png")));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(owner.body.getPosition().add(power.limit(1f)));
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.125f, 0.125f);
        sprite.setSize(0.25f, 0.25f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Entity.BULLET;
        fixtureDef.filter.maskBits = Entity.CHARACTER | Entity.TILE_BOTTOM;
		
        body.createFixture(fixtureDef);
        shape.dispose();
        body.setUserData(this);
        body.setLinearVelocity(power.scl(100).limit(this.stats.speed));
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
        public int damage = 25;
        public float speed = 15;
        public float delay = .25f;
    }
}