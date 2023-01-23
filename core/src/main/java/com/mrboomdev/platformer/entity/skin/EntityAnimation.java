package com.mrboomdev.platformer.entity.skin;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.util.Direction;
import java.util.ArrayList;
import java.util.HashMap;

public class EntityAnimation {
    private String current;
    public float progress;
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
    
    public void update(float delta) {
        progress += delta;
    }
    
	@Deprecated
    public TextureRegion getFrame(String bone) {
        if(animations.get(current).containsKey(bone))
            return animations.get(current).get(bone).animation.getKeyFrame(progress, true);
        return animations.get("idle").get(bone).animation.getKeyFrame(progress, true);
    }
	
	public AnimationObject getFrame2(String bone) {
		if(animations.get(current).containsKey(bone)) {
			animations.get(current).get(bone).progress = this.progress;
            return animations.get(current).get(bone);
		}
		animations.get("idle").get(bone).progress = this.progress;
        return animations.get("idle").get(bone);
	}
    
    public class AnimationObject {
        public TextureRegion texture;
        public String animationName, animationBone;
        public Animation<TextureRegion> animation;
        public ArrayList<AnimationFrame> frames;
        public String mode;
        public float speed = 0.05f, offset = 0.5f, progress = 0;
        public String extend;
        
        public void build(HashMap<String, AnimationObject> presents, HashMap<String, PlayMode> modes, TextureRegion texture) {
            if(presents.containsKey(extend)) {
                AnimationObject present = presents.get(extend);
                if(frames == null) frames = present.frames;
                if(speed == 999) speed = present.speed;
                if(offset == 999) offset = present.offset;
                if(mode == null) mode = present.mode;
            }
            if(mode == null) mode = "normal";
            this.texture = texture;
            Array<TextureRegion> newRegions = new Array<>();
            for(int i = 0; i < frames.size(); i++) {
                newRegions.add(new TextureRegion(texture, getBounds(i, 0), getBounds(i, 1), getBounds(i, 2), getBounds(i, 3)));
            }
            this.animation = new Animation<TextureRegion>(this.speed, newRegions, modes.get(mode));
        }
		
		public Sprite getSprite(Direction direction, Vector2 bodyPosition) {
			Sprite sprite = new Sprite(animation.getKeyFrame(progress));
			int i = animation.getKeyFrameIndex(progress);
			sprite.setSize(direction.isBackward() ? -getSize(i, 0) : getSize(i, 0), getSize(i, 1));
			sprite.setPosition(direction.isBackward()
            	? bodyPosition.x - getPos(i, 0)
            	: bodyPosition.x + getPos(i, 0),
            	 bodyPosition.y + getPos(i, 1));
			return sprite;
		}
		
		private float getPos(int id, int dir) {
			return frames.get(id).position.get(dir);
		}
		
		private float getSize(int id, int dir) {
			return frames.get(id).size.get(dir);
		}
        
        private int getBounds(int id, int bound) {
            return frames.get(id).bounds.get(bound);
        }
    }
    
    public class AnimationFrame {
        public ArrayList<Integer> bounds;
        public ArrayList<Float> size, position;
    }
}