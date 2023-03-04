package com.mrboomdev.platformer.environment;

import box2dLight.PointLight;
import box2dLight.RayHandler;
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
import com.google.gson.annotations.SerializedName;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.util.FileUtil;

public class EnvironmentBlock {
	public Sprite sprite;
	public String special;
	public float[] size;
	public float[] colission = {0, 0, 0, 0};
	public float[] shadowColission;
	private FileUtil.Source source;
	@SerializedName("texture") public String texturePath;
	private Light light;
	private Body body;
	
	public void build(World world, Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position.add(colission[2], colission[3]));
		bodyDef.type = BodyDef.BodyType.StaticBody;
		body = world.createBody(bodyDef);
		body.setUserData(this);
		
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(colission[0] / 2, colission[1] / 2,
			new Vector2(colission[2] / 2, colission[3] / 2), 0);
		fixtureDef.filter.categoryBits = Entity.BLOCK;
		fixtureDef.filter.maskBits = (short)(Entity.CHARACTER | Entity.BULLET);
		fixtureDef.shape = shape;
		body.createFixture(fixtureDef);
		
		if(shadowColission != null) {
			FixtureDef shadowFixture = new FixtureDef();
			PolygonShape shadowShape = new PolygonShape();
			shape.setAsBox(shadowColission[0] / 2, shadowColission[1] / 2,
				new Vector2(shadowColission[2], shadowColission[3]), 0);
			shadowFixture.shape = shape;
			shadowFixture.filter.categoryBits = Entity.BLOCK;
			shadowFixture.filter.maskBits = Entity.LIGHT;
			body.createFixture(shadowFixture);
			shadowShape.dispose();
		}
		shape.dispose();
	}
	
	public void setupRayHandler(RayHandler rayHandler) {
		if(light != null) {
			PointLight pointLight = new PointLight(
				rayHandler, 6,
				light.color.getColor(),
				light.distance, 0, 0);
                    
            pointLight.attachToBody(body,
                light.position[0],
                light.position[1]);
        }
	}
	
	public void setTexture(Texture texture) {
		sprite = new Sprite(texture);
		sprite.setSize(size[0], size[1]);
	}
	
	public void draw(SpriteBatch batch) {
		if(sprite == null) return;
		sprite.setCenter(body.getPosition().x + size[2], body.getPosition().y + size[3]);
		sprite.draw(batch);
	}
	
	public EnvironmentBlock cpy() {
		EnvironmentBlock copy = new EnvironmentBlock();
		copy.special = special;
		copy.colission = colission;
		copy.shadowColission = shadowColission;
		copy.size = size;
		copy.texturePath = texturePath;
		copy.sprite = new Sprite(sprite);
		return copy;
	}
	
	public class Light {
		public ColorUtil color;
		public float[] position;
		public float distance;
	}
}