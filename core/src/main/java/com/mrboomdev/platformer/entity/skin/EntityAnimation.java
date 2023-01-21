package com.mrboomdev.platformer.entity.skin;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.HashMap;

public class EntityAnimation {
    private HashMap<String, PlayMode> modes = new HashMap<>() {{
        put("normal", PlayMode.NORMAL);
    }};
    private String current;
    public HashMap<String, HashMap<String, AnimationObject>> presents, animations;
    
    public EntityAnimation build() {
        this.current = "default";
        return this;
    }
    
    public void setAnimation(String name) {
        current = animations.containsKey(name) ? name : "default";
    }
    
    public TextureRegion getFrame(String bone, Texture defaultTexture) {
        if(animations.get(current).containsKey(defaultTexture))
            return animations.get(current).get(bone).animation.getKeyFrame(0, true);
        else
            return null;
    }
    
    public class AnimationObject {
        public String animationName, animationBone;
        public Animation<TextureRegion> animation;
        public ArrayList<AnimationFrame> frames;
        public String mode;
        public int speed = 999;
        public String extend;
        
        public void build(HashMap<String, HashMap<String, AnimationObject>> presents, HashMap<String, PlayMode> modes) {
            if(presents.containsKey(animationName)) {
                AnimationObject present = presents.get(animationName).get(animationBone);
                if(frames == null) frames = present.frames;
                if(speed == 999) speed = present.speed;
                if(mode == null) mode = present.mode;
            }
            if(mode == null) mode = "normal";
            this.animation = new Animation<>(speed, null, modes.get(mode));
        }
    }
    
    public class AnimationFrame {
        public ArrayList<Float> bounds, size;
    }
}