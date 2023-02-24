package com.mrboomdev.platformer.util;

import com.badlogic.gdx.graphics.Color;

public class ColorUtil {
    public float[] values;
    
    public ColorUtil(float... values) {
        this.values = values;
    }
    
    public Color getColor() {
        return new Color(values[0], values[1], values[2], values[3]);
    }
}