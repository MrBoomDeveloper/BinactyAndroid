package com.mrboomdev.platformer.util.anime;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

@Deprecated
public class AnimeUtil {
    private final Array<AnimeStep> steps = new Array<>();
    private final Array<Integer> offsets = new Array<>();
    private boolean cancelOnStart = false;
    
    public AnimeUtil addStep(AnimeStep step, int offset) {
    	steps.add(step);
    	offsets.add(offset);
        return this;
    }
    
    public AnimeUtil cancelPreviousOnStart(boolean cancelOnStart) {
    	this.cancelOnStart = cancelOnStart;
        return this;
    }
    
    public void runAsync(Sprite... sprites) {
        for(Sprite sprite: sprites) {
            
        }
    }
    
    public void runSync(Sprite... sprites) {
    	
    }
    
    public void updateSync(float delta) {
    	
    }
}