package com.mrboomdev.platformer.util;

import com.badlogic.gdx.Gdx;

public class SizeUtil {
    
    public static class Bounds {
        public float fromX, fromY;
        public float toX, toY;
        
        public Bounds(float fromX, float fromY, float toX, float toY) {
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }
	
	public static int fitScreenWidth(int width, int padding) {
		return fitSize(width, Gdx.graphics.getWidth(), padding);
	}
	
	public static int fitScreenHeight(int height, int padding) {
		return fitSize(height, Gdx.graphics.getHeight(), padding);
	}
	
	private static int fitSize(int size, int max, int padding) {
        return size > max ? (max - padding) : size;
	}
}