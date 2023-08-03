package com.mrboomdev.platformer.environment.map;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.environment.map.tile.TileInteraction;
import com.mrboomdev.platformer.environment.map.tile.TileStyle;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Json;
import com.squareup.moshi.ToJson;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class MapTile extends MapObject {
	public String name, id;
	public int layer;
	public boolean flipX, flipY;
	public String texture, devTexture;
	public Entity.Light light;
	public float[] size;
	public float[] collision;
	public float[] position;
	public final float[] offset = {0, 0};
	public float[] scale = {1, 1};
	public int[] region;
	public TileInteraction interaction;
	public TileStyle style;
	@Json(name = "shadow_collision") public float[] shadowCollision;

	@Json(ignore = true) public boolean isSelected;
	@Json(ignore = true) public Sprite sprite, devSprite;
	@Json(ignore = true) public PointLight pointLight;
	@Json(ignore = true) public FileUtil source;
    @Json(ignore = true) public Body body;
	@Json(ignore = true) public Fixture fixture, shadowFixture;
	@Json(ignore = true) boolean isDestroyed;
	@Json(ignore = true) World world;
	@Json(ignore = true) ShapeRenderer shape;
	@Json(ignore = true)
	final GameHolder game = GameHolder.getInstance();
	
	@Override
	public void draw(SpriteBatch batch) {
		if(isDestroyed) return;

		//Update the size just to check if the texture in the camera bounds
		if(style != null && style.current != null && style.current.size != null) size = style.current.size;
		if(!isVisible()) return;

		var position = getCachedPosition();

		if(style != null) {
			sprite = style.getSprite(position, this);
			sprite.setFlip(flipX, flipY);
		}

		if(sprite != null) sprite.draw(batch);
		if(devSprite != null) devSprite.draw(batch);

		if(shape != null && isSelected) {
			batch.end();
			shape.setProjectionMatrix(game.environment.camera.combined);
			Gdx.gl.glLineWidth(3);
			shape.begin(ShapeRenderer.ShapeType.Line);
			shape.setColor(1, 1, 1, 1);

			shape.rect(
					position.x - size[0] / 2 * scale[0],
					position.y - size[1] / 2 * scale[1],
					size[0] * scale[0],
					size[1] * scale[1]);

			shape.end();
			batch.begin();
		}
	}

	private boolean isVisible() {
		var camera = game.environment.camera;
		var position = getCachedPosition();

		float viewportWidth = camera.viewportWidth * camera.zoom;
		float viewportHeight = camera.viewportHeight * camera.zoom;
		float cameraX = camera.position.x - viewportWidth / 2;
		float cameraY = camera.position.y - viewportHeight / 2;

		return (position.x - size[0] * scale[0] * .5f + size[0] * scale[0] > cameraX &&
				position.x - size[0] * scale[0] * .5f < cameraX + viewportWidth &&
				position.y - size[1] * scale[1] * .5f + size[1] * scale[1] > cameraY &&
				position.y - size[1] * scale[1] * .5f < cameraY + viewportHeight);
	}
	
	public void update() {
		if(sprite != null) {
			sprite.setFlip(flipX, flipY);
			if(scale != null && size != null) sprite.setSize(size[0] * scale[0], size[1] * scale[1]);
		}
		
		if(pointLight != null) {
			pointLight.attachToBody(body,
				flipX ? -light.offset[0] : light.offset[0],
				flipY ? -light.offset[1] : light.offset[1]);
		}

		setPosition(getPosition(false));
	}
	
	public void build(World world) {
		this.world = world;
		this.rebuild();
		Gdx.app.postRunnable(() -> shape = new ShapeRenderer());
	}
	
	public void rebuild() {
		if(body != null) {
			world.destroyBody(body);
			body = null;
		}

		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(getPosition(false));
		bodyDef.type = BodyDef.BodyType.StaticBody;
		body = world.createBody(bodyDef);
		body.setUserData(this);
		
		if(collision != null) {
			bodyDef.position.add(collision[2] * scale[0], collision[3] * scale[1]);

			PolygonShape shape = new PolygonShape();

			var center = new Vector2(
					collision[2] / 2 * scale[0],
					collision[3] / 2 * scale[1]);

			shape.setAsBox(
					collision[0] / 2 * scale[0],
					collision[1] / 2 * scale[1],
					center, 0);

			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.filter.categoryBits = Entity.TILE_BOTTOM;
			fixtureDef.filter.maskBits = Entity.CHARACTER_BOTTOM | Entity.BULLET;
			fixtureDef.shape = shape;

			fixture = body.createFixture(fixtureDef);
			shape.dispose();
		}
		
		if(shadowCollision != null) {
            PolygonShape shadowShape = new PolygonShape();

			var center = new Vector2(
					shadowCollision[2] / 2 * (flipX ? -1 : 1) * scale[0],
					shadowCollision[3] / 2 * (flipY ? -1 : 1) * scale[1]);

			shadowShape.setAsBox(
					shadowCollision[0] / 2 * scale[0],
					shadowCollision[1] / 2 * scale[1],
					center, 0);

			FixtureDef shadowFixtureDef = new FixtureDef();
			shadowFixtureDef.shape = shadowShape;
			shadowFixtureDef.filter.categoryBits = Entity.BLOCK;
			shadowFixtureDef.filter.maskBits = Entity.LIGHT;

			shadowFixture = body.createFixture(shadowFixtureDef);
			shadowShape.dispose();
		}
		
		if(interaction != null) {
			interaction.build(world, getPosition(false));
			interaction.owner = this;
		}

		updateCachedPosition();
		update();
	}
	
	public void setTexture(Texture texture, boolean isDev) {
		if(!isDev) {
			sprite = (region != null)
				? new Sprite(texture, region[0], region[1], region[2], region[3])
				: new Sprite(texture);

			if(size != null) sprite.setSize(size[0], size[1]);
			if(style != null) style.build(sprite);
		} else {
			devSprite = new Sprite(texture);
			if(size != null) devSprite.setSize(size[0], size[1]);
		}
	}

    @Override
    public void setPosition(Vector2 position) {
		body.setTransform(position, 0);

		if(sprite != null) sprite.setCenter(position.x, position.y);
		if(devSprite != null) devSprite.setCenter(position.x, position.y);
    }

	@Override
	public boolean getIsPositionUpdated() {
		return true;
	}

	@Override
    public void remove() {
		body.getWorld().destroyBody(body);
		if(pointLight != null) pointLight.remove();
	}

	@Override
	public Body getBody() {
		return body;
	}

	public void copyData(@NonNull MapTile tile) {
		collision = tile.collision;
		shadowCollision = tile.shadowCollision;
		light = tile.light;
		size = tile.size;
		texture = tile.texture;
		devTexture = tile.devTexture;
		source = tile.source;
		if(tile.style != null) {
			if(style != null)
				style.clone(tile.style);
			else
				style = new TileStyle(tile.style);
			style.owner = this;
		}
		
		if(tile.interaction != null) {
			if(interaction != null)
				interaction.clone(tile.interaction);
			else
				interaction = new TileInteraction(tile.interaction);
		}
		
		if(tile.sprite != null) {
			sprite = new Sprite(tile.sprite);
			sprite.setFlip(flipX, flipY);
			if(style != null) style.build(tile.sprite);
		}
		if(tile.devSprite != null) devSprite = new Sprite(tile.devSprite);
	}
	
	public void setupRayHandler(RayHandler rayHandler) {
		if(light != null) {
			pointLight = new PointLight(rayHandler, light.rays, light.color.getColor(), light.distance, 0, 0);
			pointLight.setContactFilter(Entity.LIGHT, Entity.NONE, Entity.BLOCK);
			pointLight.attachToBody(body,
				flipX ? -light.offset[0] : light.offset[0],
				flipY ? -light.offset[1] : light.offset[1]);
        }
	}
	
	public void setListener(TileInteraction.InteractionListener listener) {
		if(interaction == null) interaction = new TileInteraction(null);
		interaction.listener = listener;
	}
	
	@Override
    public Vector2 getPosition(boolean isBottom) {
		if(position == null && getBody() == null) return new Vector2();

		if(body == null) {
			if(cachedPosition == null) {
				cachedPosition = new Vector2(position[0] + offset[0], position[1] + offset[1]);
			}

			return cachedPosition;
		}

		return body.getPosition();
    }
	
	@Override
	public int getLayer() {
		return layer;
	}

	@SuppressWarnings("unused")
	public static class Adapter {
		@ToJson SerializedTile toJson(@NonNull MapTile tile) {
			var serialized = new SerializedTile();
			serialized.name = tile.name;
			serialized.position = tile.position;
			if(tile.style != null) serialized.style = tile.style.getSerialized();
			if(tile.interaction != null) serialized.interaction = tile.interaction.getSerialized();
			serialized.offset = (tile.offset == null || tile.offset[0] == 0 && tile.offset[1] == 0) ? null : tile.offset;
			serialized.scale = (tile.scale == null || tile.scale[0] == 1 && tile.scale[1] == 1) ? null : tile.scale;
			serialized.layer = tile.layer;
			serialized.flipX = tile.flipX ? true : null;
			serialized.flipY = tile.flipY ? true : null;
			serialized.id = (tile.id == null || tile.id.isBlank()) ? null : tile.id;
			return serialized;
		}

		private static class SerializedTile {
			public String name, id;
			public int layer;
			public Boolean flipX, flipY;
			public float[] position, offset, scale;
			public TileInteraction interaction;
			public TileStyle style;
		}
	}
}