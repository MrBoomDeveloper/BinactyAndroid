package com.mrboomdev.platformer.entity.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.google.gson.Gson;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.entity.EntityAbstract;
import com.mrboomdev.platformer.projectile.*;
import com.mrboomdev.platformer.util.CameraUtil;

public class CharacterEntity extends EntityAbstract {
	private static final float dashDuration = .25f, dashDelay = 1.5f;
	private BitmapFont font;
	private ProjectileManager projectileManager;
	private boolean isDashing;
	private float dashProgress, dashReloadProgress;
	public CharacterBrain brain;
	public CharacterSkin skin;
	public CharacterConfig config;
	public String name;
	
	public CharacterEntity(String name) {
		this.name = name;
		AssetManager asset = MainGame.getInstance().asset;
		font = asset.get("nick.ttf", BitmapFont.class);
		font.setUseIntegerPositions(false);
		font.getData().setScale(.012f, .012f);
	}
	
	public CharacterEntity create(World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(config.bodySize[0] / 2, config.bodySize[1] / 2);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		
		this.world = world;
		this.body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		body.setUserData(this);
		shape.dispose();
		
		projectileManager = new ProjectileManager(world, this)
			.setBulletConfig(new ProjectileBullet.ProjectileStats()
				.setDamage(25)
				.setAmount(100, 10)
				.setSpeed(25)
				.setRandom(1)
				.setDelay(.2f)
				.setReloadTime(1)
			).setAttackConfig(new ProjectileAttack.AttackStats()
				.setDamage(75)
				.setDelay(.4f)
				.setDuration(1));
		
		return this;
	}
	
	public CharacterEntity setConfigFromJson(String json) {
		Gson gson = new Gson();
		config = gson.fromJson(json, CharacterConfig.class).build();
		skin = gson.fromJson(Gdx.files.internal("world/player/characters/" + config.id + "/" + config.skin)
			.readString(), CharacterSkin.class).build();
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
		
		dashProgress += Gdx.graphics.getDeltaTime();
		dashReloadProgress += Gdx.graphics.getDeltaTime();
		if(isDashing && dashProgress > dashDuration) {
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
		if(isDashing || dashReloadProgress < dashDelay) return;
		dashProgress = 0;
        isDashing = true;
		body.setLinearVelocity(wasPower.scl(100).limit(18));
	}
	
	public void gainDamage(int damage) {
		config.stats.health -= damage;
		if(config.stats.health < 0) die();
		MainGame game = MainGame.getInstance();
        if(name == game.nick) CameraUtil.setCameraShake(.2f, .5f);
	}
	
	@Override
	public void usePower(Vector2 power, float speed, boolean isBot) {
		if(isDashing) return;
		super.usePower(power, speed, isBot);
	}
	
	@Override
	public void die() {
		super.die();
		MainGame game = MainGame.getInstance();
        if(name == game.nick) {
            game.toggleGameView(false);
        }
	}
}