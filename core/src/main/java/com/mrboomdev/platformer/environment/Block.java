package com.mrboomdev.platformer.environment;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mrboomdev.platformer.environment.MapBuilder;
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.util.SizeUtil.Bounds;

public class Block {
    private final int tileSize = 2;
    private Sprite sprite;
    public String special;
    public float[] size = {2, 2};
    public float[] offset = {0, 0};
    public String texture;
    public boolean colission = true;
    public float[] offset_bounds;
    public Light lights;
    
    public Block init() {
        if(texture == null) return this;
        try {
            sprite = new Sprite(new Texture(Gdx.files.internal("world/blocks/" + texture)));
        } catch(Exception e) {
            sprite = new Sprite(new Texture(Gdx.files.internal("world/blocks/error.png")));
            e.printStackTrace();
        }
        sprite.setSize(size[0], size[1]);
        return this;
    }
    
    public String build(Vector2 position, World world, RayHandler rayHandler) {
        if(colission) {
            BodyDef bodyDef = new BodyDef();
            if(offset_bounds != null) {
                bodyDef.position.set(
                    position.x + offset_bounds[0],
                    position.y + offset_bounds[1]
                        + ((size[1] - tileSize) / 2) + .4f);
            } else {
                bodyDef.position.set(position.x,
                    position.y + ((size[1] - tileSize) / 2) + .4f);
            }
                
            Body body = world.createBody(bodyDef);
            PolygonShape polygon = new PolygonShape();
            if(offset_bounds != null) {
                polygon.setAsBox(
                    size[0] / 2 + offset_bounds[2],
                    size[1] / 2 + offset_bounds[3] - .4f);
            } else {
                polygon.setAsBox(size[0] / 2, size[1] / 2 - .4f);
            }
            body.createFixture(polygon, 0);
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
    
    public void render(Vector2 position, Bounds bounds, SpriteBatch batch) {
        if(position.x - (size[0] / 2) > bounds.toX ||
            position.x + (size[0] / 2) < bounds.fromX ||
            position.y - (size[1] / 2) > bounds.toY ||
            position.y + size[1] < bounds.fromY) {
                return;
        }
        
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