package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.annotations.SerializedName;
import com.mrboomdev.platformer.util.SizeUtil.Bounds;
import java.util.HashMap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

public class EntityBody {
    private String texturePath;
    private int[] bounds;
    public HashMap<String, Bone> bones = new HashMap<>();
    public Texture texture;

    public EntityBody build(String character, World world) {
        this.texture = new Texture(Gdx.files.internal(Entity.entitiesDirectory + character + "/" + texturePath));
        for(Bone bone : bones.values()) {
            bone.build(texture);
        }
        return this;
    }
    
    public void draw(SpriteBatch batch, Vector2 position) {
        for(Bone bone : bones.values()) {
            bone.draw(batch, position);
        }
    }
    
    public class Bone {
        private TextureRegion textureRegion;
        private Sprite sprite;
        public float[] position, size, bounds;
        
        public Bone build(Texture texture) {
            this.textureRegion = new TextureRegion(texture, this.bounds[0], this.bounds[1], this.bounds[2], this.bounds[3]);
            sprite = new Sprite(textureRegion);
            sprite.setSize(this.size[0], this.size[1]);
            sprite.setPosition(this.position[0], this.position[1]);
            return this;
        }
    
        public void draw(SpriteBatch batch, Vector2 bodyPosition) {
            sprite.setPosition(bodyPosition.x + this.position[0], bodyPosition.y + this.position[1]);
            sprite.draw(batch);
        }
    }
}