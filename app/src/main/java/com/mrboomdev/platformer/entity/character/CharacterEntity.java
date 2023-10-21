package com.mrboomdev.platformer.entity.character;

import static com.mrboomdev.platformer.entity.Entity.AnimationType.ACT;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.DAMAGE;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.DASH;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.IDLE;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.RUN;
import static com.mrboomdev.platformer.entity.Entity.AnimationType.WALK;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.entity.bot.BotTarget;
import com.mrboomdev.platformer.entity.item.Item;
import com.mrboomdev.platformer.entity.item.ItemInventory;
import com.mrboomdev.platformer.environment.map.tile.TileInteraction;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.projectile.ProjectileAttack;
import com.mrboomdev.platformer.projectile.ProjectileManager;
import com.mrboomdev.platformer.util.CameraUtil;
import com.mrboomdev.platformer.util.Direction;
import com.mrboomdev.platformer.util.FunUtil;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;
import com.squareup.moshi.Json;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class CharacterEntity implements BotTarget {
	public Entity.Stats stats;
	public CharacterSkin skin;
	@Json(name = "body")
	public CharacterBody worldBody;
	@Json(ignore = true)
	public String name = "";
	@Json(ignore = true)
	public Sprite aimSprite;
	@Json(ignore = true)
	public ItemInventory inventory;
	@Json(ignore = true)
	public Fixture bottomFixture;
	@Json(ignore = true)
	public CharacterBrain brain;
	@Json(ignore = true)
	public TileInteraction nearInteraction;
	@Json(ignore = true)
	private float dashProgress, dashDuration, dashReloadProgress, staminaReloadMultiply, healthPhantom, damagedProgress = 1;
	@Json(ignore = true)
	private boolean isRunning, isDashing, isAiming;
	@Json(ignore = true)
	private final ShapeRenderer shape;
	@Json(ignore = true)
	private final BitmapFont font;
	@Json(ignore = true)
	private ProjectileManager projectileManager;
	@Json(ignore = true)
	private Vector2 damagedPower;
	@Json(ignore = true)
	private final Sprite shadow;
	@Json(ignore = true)
	private final GameHolder game = GameHolder.getInstance();
	@Json(ignore = true)
	public DamagedListener damagedListener;
	@Json(ignore = true)
	public World world;
	@Json(ignore = true)
	public Vector2 wasPower = new Vector2();
	@Json(ignore = true)
	public Body body;
	@Json(ignore = true)
	public boolean isDestroyed, isDead;
	@Json(ignore = true)
	private Vector2 cachedPosition;
	@Json(ignore = true)
	public BotTarget lookingAtTarget;
	@Json(ignore = true)
	private float progressBarProgress;
	@Json(ignore = true)
	private FileUtil source;

	public interface DamagedListener {
		void damaged(CharacterEntity attacker, int damage);
	}

	public CharacterEntity(@NonNull CharacterEntity parent) {
		this(
				parent.skin.build(parent.source),
				parent.worldBody,
				parent.stats
		);
	}
	
	public CharacterEntity(CharacterSkin skin, CharacterBody worldBody, @NonNull Entity.Stats stats) {
		this.stats = stats;
		this.worldBody = worldBody;

		this.skin = skin;
		this.skin.setOwner(this);

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

	public void setSource(FileUtil source) {
		this.source = source;
	}

	/**
	 * Creates a character in the world.
	 */
	public CharacterEntity create(@NonNull World world) {
		worldBody.build();
		var bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		
		var shape = new PolygonShape();
		shape.setAsBox(worldBody.size[0] / 2, worldBody.size[1] / 2);
		var fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = Entity.CHARACTER;
		fixtureDef.filter.maskBits = Entity.ATTACK | Entity.BULLET | Entity.INTRACTABLE;
		
		var shape3D = new PolygonShape();

		shape3D.setAsBox(
				worldBody.bottom[0] / 2, worldBody.bottom[1] / 2,
				new Vector2(worldBody.bottom[2], worldBody.bottom[3]), 0);

		var fixture3D = new FixtureDef();
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
				.setDamage(stats.damage)
				.setDelay(.4f)
				.setDuration(1));
		
		return this;
	}

	public void lookAt(BotTarget target) {
		this.lookingAtTarget = target;
	}

	public CharacterEntity setBrain(@Nullable CharacterBrain brain) {
		if(brain != null) brain.setEntity(this);

		this.brain = brain;
		return this;
	}

	public void update() {
		cachedPosition = null;
		projectileManager.clearTrash();

		if(isDead && !isDestroyed && damagedProgress > 1) {
			destroy();
		}

		if(isDestroyed) return;

		if(!isDead) {
			if(stats.stamina < stats.maxStamina) {
				stats.stamina = Math.min(stats.maxStamina, isRunning
						? stats.stamina
						: stats.stamina + staminaReloadMultiply);

				staminaReloadMultiply = Math.min(.3f, staminaReloadMultiply * 1.02f);
			}

			dashProgress += Gdx.graphics.getDeltaTime();
			dashReloadProgress += Gdx.graphics.getDeltaTime();
		}

		damagedProgress += Gdx.graphics.getDeltaTime();

		if(isDashing && dashProgress > dashDuration) {
			isDashing = false;
			dashReloadProgress = 0;
		}

		if(!isAiming && lookingAtTarget != null) {
			var item = inventory.getCurrentItem();
			if(item != null) {
				item.setPower(lookingAtTarget.getPosition().cpy().sub(getPosition()));
			}
		}

		if(damagedProgress < 1 && !isDashing) {
			body.setLinearVelocity(damagedPower != null ? damagedPower.cpy().scl(5).limit(3) : Vector2.Zero);
		} else {
			if(stats.health < stats.maxHealth) {
				healthPhantom += Gdx.graphics.getDeltaTime() / 2;
				stats.health = Math.min((int) healthPhantom, stats.maxHealth);
			}

			if(this != game.settings.mainPlayer && brain == null) {
				body.setLinearVelocity(Vector2.Zero);
				skin.setAnimation(IDLE);
			}
		}
	}

	public void draw(SpriteBatch batch) {
		update();

		if(isDestroyed) return;

		var opacity = getOpacity();
		var position = getPosition();

		shadow.setAlpha(opacity);
		shadow.setCenter(position.x, position.y - worldBody.size[1] / 2);
		shadow.draw(batch);

		var shader = game.environment.shader;
		shader.setUniformf("flashProgress", isDead
				? Math.min(damagedProgress, 1)
				: .8f - Math.min(damagedProgress * .8f, .8f));

		skin.draw(batch,  opacity);
		game.environment.batch.flush();
		shader.setUniformf("flashProgress", 0);

		inventory.draw(batch, position, skin, getDirection().isBackward());

		if(brain != null) brain.update();
	}

	public void setIsAiming(boolean isAiming) {
		if(damagedProgress < .4f || isDead) return;

		this.isAiming = isAiming;
	}

	private float getOpacity() {
		if(!isDead && damagedProgress < 1) {
			return Math.min(1.25f - (1 - damagedProgress), 1);
		} else if(isDead && damagedProgress > .5f) {
			return Math.max((1 - damagedProgress) * 2, 0);
		}

		return 1;
	}

	public void setPosition(float x, float y) {
		body.setTransform(x, y, 0);
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void drawProjectiles(SpriteBatch batch) {
		projectileManager.render(batch);
		if(isDead) return;

		var position = getPosition();
		boolean isMainPlayer = this == game.settings.mainPlayer;

		boolean isLowHealth = (damagedProgress < 3 && !isMainPlayer);
		boolean isLowEnergy = (stats.stamina < (stats.maxStamina - (stats.maxStamina / 10)) && isMainPlayer);

		if((isLowEnergy || isLowHealth) && progressBarProgress < 1) {
			progressBarProgress += Gdx.graphics.getDeltaTime() * 2;
		}

		if((!isLowEnergy && !isLowHealth) && progressBarProgress > 0) {
			progressBarProgress -= Gdx.graphics.getDeltaTime();
		}

		if(progressBarProgress > 1) progressBarProgress = 1;
		if(progressBarProgress < 0) progressBarProgress = 0;

		if(isLowHealth || (this != game.settings.mainPlayer && progressBarProgress > 0)) {
			batch.end();
			shape.setProjectionMatrix(CameraUtil.camera.combined);

			Gdx.gl.glEnable(GL20.GL_BLEND);
			shape.begin(ShapeRenderer.ShapeType.Filled); {
				shape.setColor(1, .2f, .3f, progressBarProgress);
				float progress = worldBody.size[0] / stats.maxHealth * stats.health;

				shape.rect(
						position.x - worldBody.size[0] / 2,
						position.y - worldBody.size[1] / 2 - .4f,
						progress, .175f);
				
			} shape.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
			batch.begin();
		}

		if(isLowEnergy || (this == game.settings.mainPlayer && progressBarProgress > 0)) {
			batch.end();
			shape.setProjectionMatrix(CameraUtil.camera.combined);

			Gdx.gl.glEnable(GL20.GL_BLEND);
			shape.begin(ShapeRenderer.ShapeType.Filled); {
				shape.setColor(1, 1, 1, progressBarProgress);
				float progress = worldBody.size[0] / stats.maxStamina * stats.stamina;

				shape.rect(
						position.x - worldBody.size[0] / 2,
						position.y - worldBody.size[1] / 2 - .4f,
						progress, .175f);

			} shape.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
			batch.begin();
		}

		font.draw(batch, name, position.x - 1, position.y + (worldBody.size[1] / 2) + .4f, 2, Align.center, false);
	}

	public void attack(@NonNull BotTarget target) {
		this.attack(target.getPosition().cpy().sub(getPosition()));
	}

	public void attack(Vector2 power) {
		if(isDead) return;

		var item = inventory.getCurrentItem();

		if(item != null) {
			item.attack(power, projectileManager);
			return;
		}

		projectileManager.attack(power);
	}
	
	public void interact() {
		if(nearInteraction != null && !isDead) {
			skin.setAnimation(ACT);
			FunUtil.setTimer(() -> {
				if(nearInteraction == null || isDead) return;

				nearInteraction.act();
			}, skin.getCurrentAnimationDeclaration().actionDelay);
		}
	}

	public void dash() {
		if(wasPower.isZero()) {
			dash(5, 0, Entity.DASH_DURATION);
			return;
		}

		dash(wasPower.x, wasPower.y, Entity.DASH_DURATION);
	}

	public void dash(@NonNull BotTarget target) {
		var diff = target.getPosition().cpy().sub(getPosition());
		dash(diff.x, diff.y, Entity.DASH_DURATION);
	}
	
	public void dash(float x, float y, float duration) {
		if(stats.stamina < Entity.DASH_COST || isDashing || dashReloadProgress < Entity.DASH_DELAY || isDead) return;
		
		stats.stamina -= Entity.DASH_COST;
		dashProgress = 0;
		isDashing = true;
		staminaReloadMultiply = .05f;
		this.dashDuration = duration;
		
		wasPower.set(x, y);
		body.setLinearVelocity(wasPower.cpy().scl(100).limit(22));
		skin.setAnimation(DASH);
		AudioUtil.play3DSound(game.assets.get("audio/sounds/dash.wav"), .1f, 10, getPosition());
	}

	public void setDamagedListener(DamagedListener listener) {
		this.damagedListener = listener;
	}
	
	public void gainDamage(int damage, Vector2 power) {
		if(damagedProgress < 1 || isDashing || isDead) return;
		
		AudioUtil.play3DSound(game.assets.get("audio/sounds/damage.mp3"), .25f, 10, getPosition());
		damagedProgress = (Math.random() > .75f) ? .4f : .75f;
		skin.setAnimation(DAMAGE);
		stats.health = Math.max(stats.health - damage, 0);
		healthPhantom = stats.health;
		damagedPower = power;
		isAiming = false;
		
		if(stats.health == 0) die(false);

		CameraUtil.addCameraShake(.1f, .25f);
	}
	
	public void gainDamage(int damage) {
		this.gainDamage(damage, Vector2.Zero);
	}

	public boolean giveItem(@NonNull Item item) {
		item.setOwner(this);
		boolean status = inventory.add(item);
		inventory.setCurrentItem(inventory.getCurrentItemIndex());

		return status;
	}

	public void usePower(Vector2 power, float speed) {
		if(damagedProgress < .4f || isDead) return;

		if(isDashing) {
			isRunning = true;
			return;
		}

		if(isAiming) {
			speed *= .5f;
		}

		if(power.isZero() || speed == 0) {
			isRunning = false;
			skin.setAnimation(IDLE);
			applyMovement(Vector2.Zero, 0);
			return;
		}

		wasPower = power.cpy();

		if(getSpeed(power) > speed * 5) {
			isRunning = true;
			skin.setAnimation(RUN);
			stats.stamina = Math.max(stats.stamina - .02f, 0);
			staminaReloadMultiply = .05f;

			var isEnoughPower = stats.stamina > 5;
			applyMovement(
					power.scl(isEnoughPower ? 100 : 1),
					speed / (isEnoughPower ? 1 : 2));
		} else {
			isRunning = false;
			skin.setAnimation(WALK);
			applyMovement(power.scl(100), speed / 2);
		}
	}

	public void die(boolean silently) {
		damagedProgress = 0;
		skin.setAnimationForce("death");
		isDead = true;

		var item = inventory.getCurrentItem();
		if(item != null) item.dispose();

		//if(silently) return;
		//game.script.entitiesBridge.callListener(EntitiesBridge.Function.DIED, this);
	}
	
	public static class CharacterBody {
		public float[] size;
		public float[] bottom;
		@Json(name = "light_offset")
		public float[] lightOffset;
		
		public CharacterBody build() {
			if(bottom == null) {
				bottom = new float[]{size[0], size[1], 0, 0};
			}
			return this;
		}
	}

	public void destroy() {
		if(isDestroyed) return;
		world.destroyBody(body);
		isDestroyed = true;
	}

	public float getSpeed(@NonNull Vector2 power) {
		return Math.max(
				Math.abs(power.x),
				Math.abs(power.y)
		);
	}

	public void applyMovement(@NonNull Vector2 power, float speed) {
		body.setLinearVelocity(power.cpy().limit(5).scl(speed));
	}

	public void applyMovement(float x, float y) {
		body.setLinearVelocity(x, y);
	}

	public Direction getDirection() {
		if(lookingAtTarget != null) {
			var difference = lookingAtTarget.getPosition().x - getPosition().x;
			return Direction.valueOf(difference);
		}

		return Direction.valueOf(wasPower.x);
	}

	public Vector2 getPosition() {
		if(cachedPosition == null) {
			cachedPosition = body.getPosition();
		}

		return cachedPosition;
	}
}