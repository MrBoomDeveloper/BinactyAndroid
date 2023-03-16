package com.mrboomdev.platformer.util.anime;

import com.badlogic.gdx.graphics.g2d.Sprite;

@Deprecated
public class AnimeStep {
    
    public enum Type {
        FADE,
        SCALE,
        ROTATE
    }
    
    public AnimeStep(Type type, float from, float to, int duration) {
        
    }
    
    public void run(Sprite sprite) {
        
    }
}