package com.mrboomdev.platformer.util;

public class Direction {
    public static final int NONE = 0;
    public static final int FORWARD = 1;
    public static final int BACKWARD = 2;
    public int current;
        
    public Direction(int direction) {
        current = direction;
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