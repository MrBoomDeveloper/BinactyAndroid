package com.mrboomdev.platformer.environment.map;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.util.FileUtil;
import com.squareup.moshi.Json;
import com.squareup.moshi.ToJson;
import java.util.List;

public class MapTile extends MapObject {
	public String name;
	public int layer;
	public float[] position;
	public boolean flipX, flipY;
	public String texture, devTexture;
	public Light light;
	public float[] size;
	public float[] colission;
	public float[] shadowColission;
	@Json(ignore = true) public Sprite sprite, devSprite;
	@Json(ignore = true) public PointLight pointLight;
	@Json(ignore = true) public FileUtil source;
    @Json(ignore = true) public Body body;
	@Json(ignore = true) private boolean isDestroyed;
	@Json(ignore = true) private GameHolder game = GameHolder.getInstance();
	
	@Override
	public void draw(SpriteBatch batch) {
		if(isDestroyed) return;
		var camera = game.environment.camera;

		float viewportWidth = camera.viewportWidth * camera.zoom;
		float viewportHeight = camera.viewportHeight * camera.zoom;
		float cameraX = camera.position.x - viewportWidth / 2;
		float cameraY = camera.position.y - viewportHeight / 2;

		// Check if object is within viewport
		if(getPosition(false).x - size[0] * .5f + size[0] > cameraX &&
		   getPosition(false).x - size[0] * .5f < cameraX + viewportWidth &&
    	   getPosition(false).y - size[1] * .5f + size[1] > cameraY && 
		   getPosition(false).y - size[1] * .5f < cameraY + viewportHeight) {
			if(sprite != null) sprite.draw(batch);
			if(devSprite != null) devSprite.draw(batch);
		}
	}
	
	public void build(World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(getPosition(false));
		bodyDef.type = BodyDef.BodyType.StaticBody;
		body = world.createBody(bodyDef);
		body.setUserData(this);
		
		if(colission != null) {
			bodyDef.position.add(colission[2], colission[3]);
			FixtureDef fixtureDef = new FixtureDef();
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(colission[0] / 2, colission[1] / 2,
				new Vector2(colission[2] / 2, colission[3] / 2), 0);
			fixtureDef.filter.categoryBits = Entity.TILE_BOTTOM;
			fixtureDef.filter.maskBits = Entity.CHARACTER_BOTTOM | Entity.BULLET;
			fixtureDef.shape = shape;
			body.createFixture(fixtureDef);
			shape.dispose();
		}
		
		if(shadowColission != null) {
			FixtureDef shadowFixture = new FixtureDef();
			PolygonShape shadowShape = new PolygonShape();
			shadowShape.setAsBox(shadowColission[0] / 2, shadowColission[1] / 2,
				new Vector2(shadowColission[2], shadowColission[3]), 0);
			shadowFixture.shape = shadowShape;
			shadowFixture.filter.categoryBits = Entity.BLOCK;
			shadowFixture.filter.maskBits = Entity.LIGHT;
			body.createFixture(shadowFixture);
			shadowShape.dispose();
		}
		setPosition(getPosition(false));
	}
	
	public void setTexture(Texture texture, boolean isDev) {
		if(!isDev) {
			sprite = new Sprite(texture);
			sprite.setSize(size[0], size[1]);
		} else {
			devSprite = new Sprite(texture);
			devSprite.setSize(size[0], size[1]);
		}
	}

    @Override
    public void setPosition(Vector2 position) {
        body.setTransform(position, 0);
		if(sprite != null) sprite.setCenter(body.getPosition().x, body.getPosition().y);
		if(devSprite != null) devSprite.setCenter(body.getPosition().x, body.getPosition().y);
    }

    @Override
    public void remove() {
		body.getWorld().destroyBody(body);
		if(pointLight != null) pointLight.remove();
	}
	
	public void copyData(MapTile tile) {
		colission = tile.colission;
		shadowColission = tile.shadowColission;
		light = tile.light;
		size = tile.size;
		texture = tile.texture;
		devTexture = tile.devTexture;
		source = tile.source;
		if(tile.sprite != null) {
			sprite = new Sprite(tile.sprite);
			sprite.setFlip(flipX, flipY);
		}
		if(tile.devSprite != null) devSprite = new Sprite(tile.devSprite);
	}
	
	public void setupRayHandler(RayHandler rayHandler) {
		if(light != null) {
			pointLight = new PointLight(rayHandler, light.rays, light.color.getColor(), light.distance, 0, 0);
            pointLight.attachToBody(body, flipX ? -light.offset[0] : light.offset[0], flipY ? -light.offset[1] : light.offset[1]);
			pointLight.setContactFilter(Entity.LIGHT, Entity.NONE, Entity.BLOCK);
        }
	}
	
	@Override
    public Vector2 getPosition(boolean isBottom) {
		if(body == null) {
			if(position == null) return new Vector2();
			return new Vector2(position[0], position[1] - (isBottom ? size[1] / 2 : 0));
		}
        return body.getPosition().add(0, 0/*- (isBottom ? size[1] / 2 : 0)*/);
    }
	
	@Override
	public int getLayer() {
		return layer;
	}
	
	public static class Style {
		public float speed;
		public Animation.PlayMode mode;
		public List<Frame> frames;
	}
	
	public static class Frame {
		public int[] region;
		public float[] size;
	}
	
	public static class Light {
		public ColorUtil color;
		public float distance = 5;
		public float[] offset = {0, 0};
		public int rays = 6;
	}
	
	public static class Adapter {
		@ToJson MapTile toJson(MapTile tile) {
			var serialized = new MapTile();
			serialized.name = tile.name;
			serialized.position = tile.position;
			serialized.layer = tile.layer;
			serialized.flipX = tile.flipX;
			serialized.flipY = tile.flipY;
			return serialized;
		}
	}
}