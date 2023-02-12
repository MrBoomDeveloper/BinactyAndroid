package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.Gson;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.projectile.ProjectileManager;
import com.mrboomdev.platformer.projectile.ProjectileBullet.ProjectileStats;
import com.mrboomdev.platformer.projectile.ProjectileAttack.AttackStats;

public class Entity extends EntityAbstract {
	private float dashProgress;
	public ProjectileManager projectileManager;
	public static final float dashDelay = .25f;
    public EntityConfig configNew;
    public String character;
	public boolean canWalk;
    
    public Entity(String character, World world) {
        this(character, world, new Vector2(0, 0));
    }
    
    private Entity(String character, World world, Vector2 position) {
		Gson gson = new Gson();
        this.character = character;
        this.configNew = gson.fromJson(Gdx.files.internal(character + "/manifest.json").readString(),
            EntityConfig.class).build(character, world);
        this.stats = configNew.stats;
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x, position.y);
        body = world.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.9f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        shape.dispose();
		
		projectileManager = new ProjectileManager(world, this)
			.setBulletConfig(new ProjectileStats()
				.setDamage(25)
				.setAmount(100, 10)
				.setSpeed(25)
				.setRandom(1)
				.setDelay(.2f)
				.setReloadTime(1)
			).setAttackConfig(new AttackStats()
				.setDamage(75)
				.setDelay(.4f)
				.setDuration(1));
    }
    
    public void attack() {
        projectileManager.attack();
    }
	
	public void shoot() {
        projectileManager.shoot(configNew.body.direction.isForward() ? new Vector2(5, 0) : new Vector2(-5, 0));
    }
	
	public void dash() {
		if(!canWalk) return;
		dashProgress = 0;
        canWalk = false;
		body.setLinearVelocity(wasPower.scl(100).limit(18));
    }
	
	public void shield() {
        gainDamage(-999);
    }
	
	@Override
	public void draw(SpriteBatch batch) {
		this.clearTrash();
        if(isDestroyed) return;
		dashProgress += Gdx.graphics.getDeltaTime();
		Vector2 power = body.getLinearVelocity();
        configNew.body.direction = getDirection();
		float speed = Math.max(Math.abs(power.x), Math.abs(power.y));
        configNew.animation.setAnimation((speed < .2f) ? "idle" : ((speed > 3) ? "run" : "walk"));
		if(dashProgress > dashDelay) canWalk = true;
		projectileManager.render(batch);
    }
	
	public void gainDamage(int damage) {
        stats.health -= damage;
        if(stats.health <= 0 || stats.health > 5000) die();
    }
	
	public void clearTrash() {
		projectileManager.clearTrash();
		if(isDead) destroy();
	}
    
    public void setPosition(Vector2 position) {
        if(isDead) return;
        body.setTransform(position, 0);
    }
}