package com.mrboomdev.platformer.entity.character;

import static com.mrboomdev.platformer.entity.Entity.AnimationType.DAMAGE;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.DASH;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.IDLE;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.RUN;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.WALK;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.EntityAbstract;
import com.mrboomdev.platformer.entity.bot.BotBrain;
import com.mrboomdev.platformer.entity.item.Item;
import com.mrboomdev.platformer.entity.item.ItemInventory;
import com.mrboomdev.platformer.environment.map.tile.TileInteraction;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.projectile.ProjectileAttack;
import com.mrboomdev.platformer.projectile.ProjectileManager;
import com.mrboomdev.platformer.script.bridge.EntitiesBridge;
import com.mrboomdev.platformer.util.AudioUtil;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Json;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class CharacterEntity extends EntityAbstract {
	public Entity.Stats stats;
	public CharacterSkin skin;
	@Json(name = "body") public CharacterBody worldBody;
	@Json(ignore = true) public String name;
	@Json(ignore = true) public Sprite aimSprite;
	@Json(ignore = true) public ItemInventory inventory;
	@Json(ignore = true) public Fixture bottomFixture;
	@Json(ignore = true) public CharacterBrain brain;
	@Json(ignore = true) public TileInteraction nearInteraction;
	@Json(ignore = true) float dashProgress, dashReloadProgress;
	@Json(ignore = true) float damagedProgress = 1;
	@Json(ignore = true) float staminaReloadMultiply, healthPhantom;
	@Json(ignore = true) boolean isRunning, isDashing;
	@Json(ignore = true) ShapeRenderer shape;
	@Json(ignore = true) BitmapFont font;
	@Json(ignore = true) ProjectileManager projectileManager;
	@Json(ignore = true) Vector2 damagedPower;
	@Json(ignore = true) Sprite shadow;
	@Json(ignore = true) GameHolder game = GameHolder.getInstance();
	
	public CharacterEntity cpy(String name, FileUtil source) {
		return new CharacterEntity(name, skin.build(source), worldBody, stats);
	}
	
	public CharacterEntity(String name, CharacterSkin skin, CharacterBody worldBody, @NonNull Entity.Stats stats) {
		this.name = name;
		this.stats = stats;
		this.skin = skin;
		this.worldBody = worldBody;
		shape = new ShapeRenderer();
		inventory = new ItemInventory();
		
		font = game.assets.get("nick.ttf", false);
		font.setUseIntegerPositions(false);
		font.getData().setScale(.01f, .01f);
		
		shadow = new Sprite(game.assets.get("world/effects/shadow.png", Texture.class));
		shadow.setAlpha(.75f);

		aimSprite = new Sprite(game.assets.get("ui/overlay/large_icons.png", Texture.class), 17, 33, 14, 14);
		aimSprite.setSize(.5f, .5f);
		aimSprite.setAlpha(0);
		
		healthPhantom = stats.health;
		stats.maxHealth = stats.health;
		stats.maxStamina = stats.stamina;
	}

	/**
	 * Creates character in the world.
	 * Please note, that the position should to be set manually,
	 * so i'll recommend to do it right after of the character creation.
	 */
	public CharacterEntity create(@NonNull World world) {
		worldBody.build();
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(worldBody.size[0] / 2, worldBody.size[1] / 2);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = Entity.CHARACTER;
		fixtureDef.filter.maskBits = Entity.ATTACK | Entity.BULLET | Entity.INTRACTABLE;
		
		PolygonShape shape3D = new PolygonShape();
		shape3D.setAsBox(worldBody.bottom[0] / 2, worldBody.bottom[1] / 2,
			new Vector2(worldBody.bottom[2], worldBody.bottom[3]), 0);
		FixtureDef fixture3D = new FixtureDef();
		fixture3D.shape = shape3D;
		fixture3D.filter.categoryBits = Entity.CHARACTER_BOTTOM;
		fixture3D.filter.maskBits = Entity.TILE_BOTTOM;
		
		this.world = world;
		this.body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		bottomFixture = body.createFixture(fixture3D);
		body.setUserData(this);
		
		shape.dispose();
		shape3D.dispose();
		shadow.setSize(worldBody.bottom[0], worldBody.bottom[1]);
		
		projectileManager = new ProjectileManager(world, this)
			.setAttackConfig(new ProjectileAttack.AttackStats()
				.setDamage(50)
				.setDelay(.4f)
				.setDuration(1));
		
		return this;
	}

	public CharacterEntity setBrain(@NonNull CharacterBrain brain) {
		brain.setEntity(this);
		this.brain = brain;
		return this;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		projectileManager.clearTrash();
		if(isDead && !isDestroyed) destroy();
		if(isDestroyed) return;
		
		stats.stamina = Math.min(stats.maxStamina, isRunning
			? stats.stamina
			: stats.stamina + staminaReloadMultiply);
		
		dashProgress += Gdx.graphics.getDeltaTime();
		dashReloadProgress += Gdx.graphics.getDeltaTime();
		staminaReloadMultiply = Math.min(.3f, staminaReloadMultiply * 1.02f);
		damagedProgress += Gdx.graphics.getDeltaTime();
		
		if(isDashing && dashProgress > Entity.DASH_DURATION) {
			isDashing = false;
			dashReloadProgress = 0;
		}
		if(damagedProgress < 1 && !isDashing) {
			body.setLinearVelocity(damagedPower.scl(5).limit(3));
		} else {
			healthPhantom += Gdx.graphics.getDeltaTime() / 2;
			stats.health = Math.min((int)healthPhantom, stats.maxHealth);
		}
		if(brain != null) brain.update();
		
		shadow.setCenter(getPosition().x, getPosition().y - worldBody.size[1] / 2);
		shadow.draw(batch);
		skin.draw(batch, getPosition(), getDirection(), this);
		inventory.draw(batch, getPosition(), skin, getDirection().isBackward());

		//drawDebug(batch);
	}

	/**
	 * If character has a BotBrain, then will draw all the paths where it can walk.
	 * Try to use this method as less possible, because it uses a lot of resources to draw everything.
	 */
	public void drawDebug(SpriteBatch batch) {
		shape.setProjectionMatrix(game.environment.camera.combined);
		if(brain != null && brain instanceof BotBrain) {
			var botBrain = (BotBrain)brain;
			if(botBrain.graph == null) return;
			if(botBrain.graph.connections == null) return;
			batch.end();
			for(var connection : botBrain.graph.connections) {
				shape.begin(ShapeRenderer.ShapeType.Filled);
				shape.setColor(1, 1, 1, 1);
				shape.rectLine(connection.getFromNode().position.x, connection.getFromNode().position.y, connection.getToNode().position.x, connection.getToNode().position.y, .1f);
				shape.end();
			}
			batch.begin();
		}
	}
	
	public void drawProjectiles(SpriteBatch batch) {
		projectileManager.render(batch);
		if(isDead) return;
		batch.end();
		if(damagedProgress < 3 && this != game.settings.mainPlayer) {
			shape.setProjectionMatrix(game.environment.camera.combined);
			shape.begin(ShapeRenderer.ShapeType.Filled);
			shape.setColor(1, 0, 0, 1);
			float progress = worldBody.size[0] / stats.maxHealth * stats.health;
			shape.rect(
				getPosition().x - worldBody.size[0] / 2,
				getPosition().y - worldBody.size[1] / 2 - .4f,
				progress, .2f);
			shape.end();
		}
		batch.begin();
		font.draw(batch, name, getPosition().x - 1, getPosition().y + (worldBody.size[1] / 2) + .4f, 2, Align.center, false);
	}

	public void attack(Vector2 power) {
		if(isDead) return;
		if(inventory.items.size > inventory.current) {
			inventory.getCurrentItem().attack(power.scl(.2f), projectileManager);
			return;
		}

		projectileManager.attack(power.scl(.2f));
	}
	
	public void interact() {
		if(nearInteraction != null) {
			nearInteraction.act();
		}
	}
	
	public void dash() {
		if(stats.stamina < Entity.DASH_COST) return;
		if(isDashing || dashReloadProgress < Entity.DASH_DELAY) return;
		
		stats.stamina -= Entity.DASH_COST;
		dashProgress = 0;
		isDashing = true;
		staminaReloadMultiply = .05f;
		
		if(wasPower.isZero()) wasPower = new Vector2(5, 0);
		body.setLinearVelocity(wasPower.scl(100).limit(18));
		AudioUtil.play3DSound(game.assets.get("audio/sounds/dash.wav"), .1f, 10, getPosition());
		
	}
	
	public void gainDamage(int damage, Vector2 power) {
		if(damagedProgress < 1 || isDashing) return;
		
		AudioUtil.play3DSound(game.assets.get("audio/sounds/damage.mp3", Sound.class), .25f, 10, getPosition());
		damagedProgress = (Math.random() > .8f) ? 0 : .8f;
		skin.setAnimation(DAMAGE);
		stats.health = Math.max(stats.health - damage, 0);
		healthPhantom = stats.health;
		damagedPower = power;
		
		if(stats.health == 0) die(false);
		CameraUtil.addCameraShake(.1f, .25f);
	}
	
	public void gainDamage(int damage) {
		this.gainDamage(damage, Vector2.Zero);
	}

	public boolean giveItem(Item item) {
		return inventory.add(item);
	}
	
	@Override
	public void usePower(Vector2 power, float speed, boolean isBot) {
		if(damagedProgress < .4f) return;
		if(isDashing) {
			isRunning = true;
			skin.setAnimation(DASH);
			return;
		}
		if(power.isZero() || speed == 0) {
			isRunning = false;
			skin.setAnimation(IDLE);
			super.usePower(Vector2.Zero, 0, false);
		} else if(getSpeed(power) > speed * 5) {
			isRunning = true;
			skin.setAnimation(RUN);
			stats.stamina = Math.max(stats.stamina - .02f, 0);
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
	public void die(boolean silently) {
		super.die(silently);
		if(silently) return;
		game.script.entitiesBridge.callListener(EntitiesBridge.Function.DIED, this);
	}
	
	public static class CharacterBody {
		public float[] size;
		public float[] bottom;
		@Json(name = "light_offset") public float[] lightOffset;
		
		public CharacterBody build() {
			if(bottom == null) {
				bottom = new float[]{size[0], size[1], 0, 0};
			}
			return this;
		}
	}
	
	/* Don't use this. It is only for the CharacterGroup */
	protected CharacterEntity() {}
}