package com.mrboomdev.platformer.entity.skin;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.HashMap;

public class EntityAnimation {
    private String current;
    public HashMap<String, HashMap<String, AnimationObject>> animations;
    public HashMap<String, AnimationObject> presents;
    public HashMap<String, PlayMode> modes = new HashMap<>() {{
        put("normal", PlayMode.NORMAL);
        put("reverse", PlayMode.REVERSED);
        put("loop", PlayMode.LOOP);
        put("loop_pingpong", PlayMode.LOOP_PINGPONG);
        put("loop_random", PlayMode.LOOP_RANDOM);
        put("loop_reverse", PlayMode.LOOP_REVERSED);
    }};
    
    public EntityAnimation build() {
        this.current = "idle";
        return this;
    }
    
    public void setAnimation(String name) {
        current = animations.containsKey(name) ? name : "idle";
    }
    
    public TextureRegion getFrame(String bone, Texture defaultTexture) {
        if(animations.get(current).containsKey(defaultTexture))
            return animations.get(current).get(bone).animation.getKeyFrame(0, true);
        else
            return null;
    }
    
    public class AnimationObject {
        public Texture texture;
        public String animationName, animationBone;
        public Animation<TextureRegion> animation;
        public ArrayList<AnimationFrame> frames;
        public String mode;
        public float speed = 999, offset = 999;
        public String extend;
        
        public void build(HashMap<String, AnimationObject> presents, HashMap<String, PlayMode> modes, Texture texture) {
            if(presents.containsKey(animationName)) {
                AnimationObject present = presents.get(extend);
                if(frames == null) frames = present.frames;
                if(speed == 999) speed = present.speed;
                if(offset == 999) offset = present.offset;
                if(mode == null) mode = present.mode;
            }
            if(mode == null) mode = "normal";
            this.texture = texture;
            System.out.println(animationName + ":" + animationBone);
            TextureRegion[] regions = new TextureRegion[frames.size()];
            for(int i = 0; i < frames.size(); i++) {
                regions[i] = new TextureRegion(texture, getBounds(i, 0), getBounds(i, 1), getBounds(i, 2), getBounds(i, 3));
            }
            this.animation = new Animation<TextureRegion>(speed, regions);
        }
        
        private int getBounds(int id, int bound) {
            return frames.get(id).bounds.get(bound);
        }
    }
    
    public class AnimationFrame {
        public ArrayList<Integer> bounds;
        public ArrayList<Float> size;
    }
}