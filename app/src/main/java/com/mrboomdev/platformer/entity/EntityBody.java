package com.mrboomdev.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mrboomdev.platformer.entity.skin.EntityAnimation;
import com.mrboomdev.platformer.entity.skin.EntityAnimation.AnimationObject;
import com.mrboomdev.platformer.util.Direction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

public class EntityBody {
    private String texturePath;
    private ArrayList<Float> bounds;
    private Vector2 wasPosition = new Vector2(0, 0);
    private EntityAnimation animations;
    public HashMap<String, Bone> bones = new HashMap<>();
    public Texture texture;
    public Direction direction = new Direction(Direction.FORWARD);
    public Body body;

    public EntityBody build(String character, World world, EntityAnimation animations) {
        this.animations = animations;
        this.texture = new Texture(Gdx.files.internal(character + "/" + texturePath));
        for(HashMap.Entry entry : bones.entrySet()) {
            ((Bone)entry.getValue()).build(texture, animations, (String) entry.getKey());
        }
        return this;
    }

    public void draw(SpriteBatch batch, Vector2 position) {
        animations.update(Gdx.graphics.getDeltaTime());
        ArrayList<Bone> array = new ArrayList<>(bones.values());
        Collections.sort(array);
        for(Bone bone : array) {
            bone.draw(batch, position, direction);
        }
    }

    public class Bone implements Comparable<Bone> {
        private TextureRegion texture;
        private Sprite sprite;
        private EntityAnimation animations;
        public TextureRegion textureRegion;
        public ArrayList<Float> position, size;
        public ArrayList<Integer> bounds;
        public boolean animated = false;
        public int layer = 0;
        public String name;

        public Bone build(Texture texture, EntityAnimation animations, String name) {
            this.name = name;
            this.texture = new TextureRegion(texture);
            this.animations = animations;
            this.textureRegion = new TextureRegion(texture, bounds.get(0), bounds.get(1), bounds.get(2), bounds.get(3));
            if(!animated) {
                sprite = new Sprite(textureRegion);
                sprite.setSize(size.get(0), size.get(1));
                sprite.setPosition(position.get(0), position.get(1));
                return this;
            }
            buildAnimations();
            return this;
        }

        private void buildAnimations() {
            for(HashMap.Entry<String, HashMap<String, AnimationObject>> animation : animations.animations.entrySet()) {
                AnimationObject object = (animation.getValue().containsKey(name))
                    ? animation.getValue().get(name)
                    : animations.animations.get("idle").get(name);
                object.animationName = animation.getKey();
                object.animationBone = name;
                object.build(animations.presents, animations.modes, textureRegion);
            }
        }

        public void draw(SpriteBatch batch, Vector2 bodyPosition, Direction direction) {
            if(!animated) {
				sprite.setSize(direction.isBackward() ? -size.get(0) : size.get(0), size.get(1));
				sprite.setPosition(direction.isBackward()
            		? bodyPosition.x - position.get(0)
                	: bodyPosition.x + position.get(0),
                 	bodyPosition.y + position.get(1));
			} else {
				AnimationObject object = animations.getFrame2(name);
				sprite = object.getSprite(direction, bodyPosition, new Vector2(position.get(0), position.get(1)));
			}
            sprite.draw(batch);
        }

        @Override
        public int compareTo(Bone bone) {
            return this.layer - bone.layer;
        }
    }
}