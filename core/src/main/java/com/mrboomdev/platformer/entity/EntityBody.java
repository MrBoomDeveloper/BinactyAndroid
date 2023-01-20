package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.util.Direction;
import java.util.ArrayList;
import java.util.TreeMap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

public class EntityBody {
    private String texturePath;
    private ArrayList<Float> bounds;
    private Vector2 wasPosition = new Vector2(0, 0);
    public TreeMap<String, Bone> bones = new TreeMap<>();
    public Texture texture;
    public Direction direction = new Direction(Direction.FORWARD);

    public EntityBody build(String character, World world) {
        this.texture = new Texture(Gdx.files.internal(Entity.entitiesDirectory + character + "/" + texturePath));
        for(Bone bone : bones.values()) {
            bone.build(texture);
        }
        return this;
    }
    
    public void draw(SpriteBatch batch, Vector2 position) {
        for(Bone bone : bones.values()) {
            bone.draw(batch, position, direction);
        }
    }
    
    public class Bone implements Comparable<Bone> {
        private TextureRegion textureRegion;
        private Sprite sprite;
        public int layer = 0;
        public ArrayList<Float> position, size;
        public ArrayList<Integer> bounds;
        
        public Bone build(Texture texture) {
            this.textureRegion = new TextureRegion(texture, bounds.get(0), bounds.get(1), bounds.get(2), bounds.get(3));
            sprite = new Sprite(textureRegion);
            sprite.setSize(size.get(0), size.get(1));
            sprite.setPosition(position.get(0), position.get(1));
            return this;
        }
    
        public void draw(SpriteBatch batch, Vector2 bodyPosition, Direction direction) {
            sprite.setSize(direction.isBackward() ? -size.get(0) : size.get(0), size.get(1));
            //sprite.setPosition(bodyPosition.x + position.get(0), bodyPosition.y + position.get(1));
            sprite.setPosition((direction.isBackward()
                ? bodyPosition.x - position.get(0)
                : bodyPosition.x + position.get(0)),
                  bodyPosition.y + position.get(1));
            sprite.draw(batch);
        }
        
        @Override
        public int compareTo(Bone bone) {
            return this.layer - bone.layer;
        }
    }
}