package com.mrboomdev.platformer.util;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

public class AnimationUtil {
    private UpdateHandler handler;
    private Array<Sprite> sprites = new Array<Sprite>();
    //private Array<Keyframe> keyframes = new Array<Keyframe>();
    
    public AnimationUtil handleUpdate(UpdateHandler handler) {
        this.handler = handler;
        return this;
    }
    
    public AnimationUtil init(UpdateHandler handler) {
        for(Sprite sprite : sprites) {
            handler.update(sprite, 0);
        }
        return this;
    }
    
    public AnimationUtil attach(Sprite sprite) {
        sprites.add(sprite);
        return this;
    }
    
    /*public AnimationUtil addKeyframe() {
        return this;
    }*/
    
    /*public class Keyframe {
        private 
        
        public Keyframe() {
            
        }
    }*/
    
    public void update(float progress) {
        for(Sprite sprite : sprites) {
            handler.update(sprite, progress);
        }
    }
    
    public interface UpdateHandler {
        public void update(Sprite sprite, float progress);
    }
}