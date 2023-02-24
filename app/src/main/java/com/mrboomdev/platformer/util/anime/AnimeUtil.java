package com.mrboomdev.platformer.util.anime;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.util.anime.AnimeStep;

public class AnimeUtil {
    private Array<AnimeStep> steps = new Array<>();
    private Array<Integer> offsets = new Array<>();
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