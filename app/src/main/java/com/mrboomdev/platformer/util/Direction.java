package com.mrboomdev.platformer.util;

import androidx.annotation.Nullable;
public class Direction {
    public static final Direction FORWARD = Direction.valueOf(1);
    public static final Direction BACKWARD = Direction.valueOf(-1);
    public int current = 0;

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof Direction)) return false;

        return current == ((Direction) obj).current;
    }

    public static Direction valueOf(float lookAt) {
        if(isInitialized()) {
            return (lookAt >= 0) ? FORWARD : BACKWARD;
        }

        return new Direction(lookAt);
    }

    private static boolean isInitialized() {
        return FORWARD != null && BACKWARD != null;
    }
	
	public Direction(float lookAt) {
		setFrom(lookAt);
	}
    
    public Direction reverse() {
        return Direction.valueOf(current * -1);
    }
    
    public void setFrom(float x) {
        current = x >= 0 ? 1 : -1;
    }
    
    public boolean isForward() {
        return current == 1;
    }
    
    public boolean isBackward() {
        return current == -1;
    }
}