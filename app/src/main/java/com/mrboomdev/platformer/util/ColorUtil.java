package com.mrboomdev.platformer.util;

import com.badlogic.gdx.graphics.Color;
import com.google.gson.annotations.Expose;

public class ColorUtil {
    @Expose public float[] values;
    
    public ColorUtil(float... values) {
        this.values = values;
    }
	
	public static ColorUtil parse(String text) {
		var instance = new ColorUtil();
		text = text.replaceAll(" ", "");
		String[] arrayText = text.split(",");
		float[] arrayNumber = new float[4];
		for(int i = 0; i < 4; i++) {
			arrayNumber[i] = Float.parseFloat(arrayText[i]);
		}
		instance.values = arrayNumber;
		return instance;
	}
    
    public Color getColor() {
        return new Color(values[0], values[1], values[2], values[3]);
    }
	
	@Override
	public String toString() {
		return values[0] + ", " + values[1] + ", " + values[2] + ", " + values[3];
	}
}