package com.mrboomdev.platformer.environment;

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
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.util.FileUtil;

public class EnvironmentBlock {
	public Sprite sprite;
	public String special;
	public float[] size;
	public float[] colission = {0, 0, 0, 0};
	public boolean createShadow = true;
	private FileUtil.Source source;
	@SerializedName("texture") public String texturePath;
	private Light light;
	private Body body;
	public String parentPath;
	
	public EnvironmentBlock init(String parentPath) {
		this.parentPath = parentPath;
		return this;
	}
	
	public void build(World world, Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position.add(colission[2], colission[3]));
		bodyDef.type = BodyDef.BodyType.StaticBody;
		body = world.createBody(bodyDef);
		body.setUserData(this);
		
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(colission[0] / 2, colission[1] / 2);
		fixtureDef.shape = shape;
		body.createFixture(fixtureDef);
		shape.dispose();
		
		Gdx.app.postRunnable(() -> {
			if(texturePath != null) {
				sprite = new Sprite(new Texture(Gdx.files.internal(parentPath + texturePath)));
				sprite.setSize(size[0], size[1]);
			}
		});
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
		copy.createShadow = createShadow;
		copy.size = size;
		copy.texturePath = texturePath;
		copy.parentPath = parentPath;
		//copy.sprite = new Sprite(sprite);
		return copy;
	}
	
	public class Light {
		public ColorUtil color;
		public float[] position;
		public float distance;
	}
}