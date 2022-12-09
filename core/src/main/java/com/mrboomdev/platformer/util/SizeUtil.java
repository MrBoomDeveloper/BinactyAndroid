package com.mrboomdev.platformer.util;

import com.badlogic.gdx.Gdx;

public class SizeUtil {
	
	public static int fitScreenWidth(int width, int padding) {
		return fitSize(width, Gdx.graphics.getWidth(), padding);
	}
	
	public static int fitScreenHeight(int height, int padding) {
		return fitSize(height, Gdx.graphics.getHeight(), padding);
	}
	
	private static int fitSize(int size, int max, int padding) {
		if(size > max) {
			return max - padding;
		}
		return size;
	}
}
