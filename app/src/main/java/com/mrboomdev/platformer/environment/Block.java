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
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.util.ColorUtil;

public class Block {
    private static final float tileSize = 2;
    private Sprite sprite;
	private boolean ignoreLights;
    public String special;
    public float[] size = {2, 2};
    public float[] offset = {0, 0};
    public String texture;
    public boolean colission = true;
    public float[] offset_bounds = {0, 0, 0, 0};
    public Light lights;
    
    public Block init() {
        if(texture == null) return this;
        sprite = new Sprite(new Texture(Gdx.files.internal("world/blocks/" + texture)));
        sprite.setSize(size[0], size[1]);
        return this;
    }
    
    public String build(Vector2 position, World world, RayHandler rayHandler) {
        if(colission) {
            BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(position.add(offset_bounds[0],
				offset_bounds[1] + ((size[1] - tileSize) / 2) + .4f));
			
            Body body = world.createBody(bodyDef);
            body.setUserData(this);
			FixtureDef fixtureDef = new FixtureDef();
            PolygonShape polygon = new PolygonShape();
			polygon.setAsBox(
				size[0] / 2 + offset_bounds[2],
				size[1] / 2 + offset_bounds[3] - .4f);
			
			fixtureDef.shape = polygon;
			fixtureDef.filter.categoryBits = Entity.BLOCK | Entity.TILE_BOTTOM;
			if(ignoreLights) {
				fixtureDef.filter.maskBits = Entity.CHARACTER | Entity.CHARACTER_BOTTOM | Entity.BULLET;
			} else {
				fixtureDef.filter.maskBits = Entity.CHARACTER | Entity.CHARACTER_BOTTOM | Entity.BULLET | Entity.LIGHT;
			}
            body.createFixture(fixtureDef);
            polygon.dispose();
            
            if(lights != null) {
                PointLight light = new PointLight(
                    rayHandler, 6,
                    lights.color.getColor(),
                    lights.distance, 0, 0);
                    
                light.attachToBody(body,
                    lights.offset[0],
                    lights.offset[1]);
            }
        }
        return special;
    }
    
    public void render(Vector2 position, SpriteBatch batch) {
        if(sprite != null) {
            batch.draw(sprite, position.x - (tileSize / 2) + offset[0], 
                position.y - (tileSize / 2) + offset[1], 
                size[0], size[1]);
        }
    }
    
    public class Light {
        public float[] offset = {0, 0};
        public float distance = 2;
        public ColorUtil color = new ColorUtil(255, 255, 255, 1);
    }
}