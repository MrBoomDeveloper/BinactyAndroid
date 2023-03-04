package com.mrboomdev.platformer.entity.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.google.gson.Gson;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.EntityAbstract;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.projectile.*;
import com.mrboomdev.platformer.util.CameraUtil;
import static com.mrboomdev.platformer.entity.Entity.Animation.*;
import com.mrboomdev.platformer.util.FileUtil;

public class CharacterEntity extends EntityAbstract {
	private BitmapFont font;
	private ProjectileManager projectileManager;
	private boolean isDashing;
	private float dashProgress, dashReloadProgress;
	private float staminaReloadMultiply;
	private boolean isRunning;
	public Fixture bottomFixture;
	public CharacterConfig.Stats stats;
	public CharacterBrain brain;
	public CharacterSkin skin;
	public CharacterConfig config;
	public String name;
	
	public CharacterEntity(String name) {
		this.name = name;
		AssetManager asset = GameHolder.getInstance().assets;
		font = asset.get("nick.ttf", BitmapFont.class);
		font.setUseIntegerPositions(false);
		font.getData().setScale(.012f, .012f);
	}
	
	public CharacterEntity create(World world) {
		if(config.body3D == null) {
			config.body3D = new float[]{config.bodySize[0], config.bodySize[1], 0, 0};
		}
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(config.bodySize[0] / 2, config.bodySize[1] / 2);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		
		PolygonShape shape3D = new PolygonShape();
		shape3D.setAsBox(config.body3D[0] / 2, config.body3D[1] / 2,
			new Vector2(config.body3D[2], config.body3D[3]), 0);
		FixtureDef fixture3D = new FixtureDef();
		fixture3D.shape = shape3D;
		
		this.world = world;
		this.body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		bottomFixture = body.createFixture(fixture3D);
		body.setUserData(this);
		
		shape.dispose();
		shape3D.dispose();
		
		projectileManager = new ProjectileManager(world, this)
			.setBulletConfig(new ProjectileBullet.ProjectileStats()
				.setDamage(15)
				.setAmount(100, 10)
				.setSpeed(25)
				.setRandom(1)
				.setDelay(.2f)
				.setReloadTime(1)
			).setAttackConfig(new ProjectileAttack.AttackStats()
				.setDamage(50)
				.setDelay(.4f)
				.setDuration(1));
		
		return this;
	}
	
	public CharacterEntity setConfig(FileUtil file) {
		Gson gson = new Gson();
		
		config = gson.fromJson(
			file.goTo("manifest.json").readString(),
			CharacterConfig.class)
		.build();
		
		skin = gson.fromJson(
			file.goTo("skin.json").readString(),
			CharacterSkin.class)
		.build(file);
		
		stats = config.stats;
		return this;
	}
	
	public CharacterEntity setBrain(CharacterBrain brain) {
		brain.setEntity(this);
		this.brain = brain;
		return this;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		projectileManager.clearTrash();
		if(isDead && !isDestroyed) destroy();
		
		if(isDestroyed) return;
		skin.draw(batch, body.getPosition(), getDirection());
		
		stats.stamina = Math.min(stats.maxStamina, isRunning ? stats.stamina : stats.stamina + staminaReloadMultiply);
		
		dashProgress += Gdx.graphics.getDeltaTime();
		dashReloadProgress += Gdx.graphics.getDeltaTime();
		staminaReloadMultiply = Math.min(.3f, staminaReloadMultiply * 1.02f);
		if(isDashing && dashProgress > Entity.DASH_DURATION) {
			isDashing = false;
			dashReloadProgress = 0;
		}
		if(brain != null) brain.update();
	}
	
	public void drawProjectiles(SpriteBatch batch) {
		projectileManager.render(batch);
		if(isDead) return;
		font.draw(batch, name,
			body.getPosition().x - 1,
			body.getPosition().y + (config.bodySize[1] / 2) + .4f,
			2, Align.center, false);
	}

	public void attack(Vector2 power) {
		projectileManager.attack();
	}
	
	public void shoot(Vector2 power) {
		projectileManager.shoot(getDirection().isForward() ? new Vector2(5, 0) : new Vector2(-5, 0));
	}
	
	public void dash() {
		if(stats.stamina < Entity.DASH_COST) return;
		if(isDashing || dashReloadProgress < Entity.DASH_DELAY) return;
		config.stats.stamina -= Entity.DASH_COST;
		dashProgress = 0;
        isDashing = true;
		staminaReloadMultiply = .05f;
		body.setLinearVelocity(wasPower.scl(100).limit(18));
	}
	
	public void gainDamage(int damage) {
		config.stats.health -= damage;
		if(config.stats.health <= 0) die();
        if(name == GameHolder.getInstance().settings.playerName) {
			CameraUtil.setCameraShake(.2f, .5f);
		}
	}
	
	@Override
	public void usePower(Vector2 power, float speed, boolean isBot) {
		if(isDashing) {
			isRunning = true;
			skin.setAnimation(DASH);
			return;
		}
		if(power.isZero() || speed == 0) {
			isRunning = false;
			skin.setAnimation(IDLE);
			super.usePower(Vector2.Zero, 0, false);
		} else if(getSpeed(power) > speed * 6) {
			isRunning = true;
			skin.setAnimation(RUN);
			stats.stamina = Math.max(stats.stamina - .05f, 0);
			staminaReloadMultiply = .05f;
			if(stats.stamina > 5) {
				super.usePower(power.scl(100), speed, isBot);
			} else {
				super.usePower(power, speed / 2, isBot);
			}
		} else {
			isRunning = false;
			skin.setAnimation(WALK);
			super.usePower(power.scl(100), speed / 2, isBot);
		}
	}
	
	@Override
	public void die() {
		super.die();
		var game = GameHolder.getInstance();
        if(name == game.settings.playerName) {
            game.launcher.exit();
        }
	}
}