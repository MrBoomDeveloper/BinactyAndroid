package com.mrboomdev.platformer.environment.map;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.util.FileUtil;

public class MapTile extends MapObject {
	@SerializedName("texture") public String texturePath;
	@SerializedName("devTexture") public String devTexturePath;
	@Expose public String name;
	@Expose public int layer;
	@Expose public float[] position;
	private Light light;
	public float[] size;
	public float[] colission;
	public float[] shadowColission;
	public Sprite sprite, devSprite;
	public FileUtil source;
    private Body body;
	private boolean isDestroyed;
	private PointLight pointLight;
	private GameHolder game = GameHolder.getInstance();
	
	@Override
	public void draw(SpriteBatch batch) {
		if(isDestroyed) return;
		if(sprite != null) sprite.draw(batch);
		if(devSprite != null) devSprite.draw(batch);
	}
	
	public void build(World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(getPosition());
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
		setPosition(getPosition());
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
		texturePath = tile.texturePath;
		devTexturePath = tile.devTexturePath;
		source = tile.source;
		if(tile.sprite != null) sprite = new Sprite(tile.sprite);
		if(tile.devSprite != null) devSprite = new Sprite(tile.devSprite);
	}
	
	public void setupRayHandler(RayHandler rayHandler) {
		if(light != null) {
			this.pointLight = new PointLight(
				rayHandler, 6,
				light.color.getColor(),
				light.distance, 0, 0);
                    
            this.pointLight.attachToBody(body,
                light.offset[0],
                light.offset[1]);
        }
	}
	
	@Override
    public Vector2 getPosition() {
		if(body == null) {
			if(position == null) return new Vector2();
			return new Vector2(position[0], position[1]);
		}
        return body.getPosition();
    }
	
	@Override
	public int getLayer() {
		return layer;
	}
	
	public class Light {
		public ColorUtil color;
		public float distance = 5;
		public float[] offset = {0, 0};
	}
}