package com.mrboomdev.platformer.util;
import com.badlogic.gdx.math.Vector2;

public class Direction {
    public static final int NONE = 0;
    public static final int FORWARD = 1;
    public static final int BACKWARD = 2;
    public int current = NONE;
        
    public Direction(int direction) {
        current = direction;
    }
	
	public Direction(float lookAt) {
		setFrom(lookAt);
	}
    
    public Direction reverse() {
        return new Direction(current == 1 ? 2 : 1);
    }
    
    public void setFrom(float x) {
        current = x >= 0 ? FORWARD : BACKWARD;
    }
    
    public boolean isForward() {
        return current == FORWARD;
    }
    
    public boolean isBackward() {
        return current == BACKWARD;
    }
}