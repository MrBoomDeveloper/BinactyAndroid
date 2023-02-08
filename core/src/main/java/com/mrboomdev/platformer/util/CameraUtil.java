package com.mrboomdev.platformer.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class CameraUtil {
    private static float shakePower, shakeDuration, shakeProgress;
    public static Vector2 shakeCurrent = new Vector2(0, 0);
    
    public static void setCameraShake(float power, float duration) {
        shakePower = power;
        shakeDuration = duration;
        shakeProgress = 0;
        shakeCurrent = new Vector2(0, 0);
    }
    
    public static Vector2 getCameraShake() {
        return shakeCurrent;
    }
    
    public static void update(float delta) {
        shakeProgress += delta;
        if(shakeProgress < shakeDuration - (shakeDuration / 5)) {
            shakeCurrent.set(new Vector2(
                    (float)(Math.random() * shakePower - (shakePower / 2)), 
                    (float)Math.random() * shakePower - (shakePower / 2)));
        } else {
            shakeCurrent = new Vector2(0, 0);
        }
    }
}