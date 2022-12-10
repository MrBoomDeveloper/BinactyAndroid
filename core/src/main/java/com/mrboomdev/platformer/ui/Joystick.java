package com.mrboomdev.platformer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.ui.Joystick;

public class Joystick {
    private SpriteBatch sprites;
    private Array<Texture> joystickTextures = new Array<Texture>();
    private final int joystickOffset = 75, joystickSize = 225;
    private int touchX = 0, touchY = 0;
    public float powerX = 0, powerY = 0;
	public boolean isActive = false;
	private int screenHeight;
	private Vector2 position;
    
    public Joystick(SpriteBatch sprites, int screenHeight) {
		this.screenHeight = screenHeight;
        this.sprites = sprites;
        joystickTextures.add(new Texture(Gdx.files.internal("img/ui/joystick_holder.png")));
        joystickTextures.add(new Texture(Gdx.files.internal("img/ui/joystick.png")));
        reset();
    }
    
    public void render() {
        sprites.draw(joystickTextures.get(0), joystickOffset, joystickOffset, joystickSize - 25, joystickSize);
        sprites.draw(joystickTextures.get(1), joystickOffset + touchX - 25, joystickOffset + touchY - 32, 55, 65);
    }
    
    public void update(int x, int y) {
        //Fix reverse y
        y = y * -1 + screenHeight;
        
		isActive = true;
        if(x > joystickOffset + joystickSize - 40) {
            touchX = joystickOffset + (joystickSize / 2);
        } else if(x < joystickOffset + 5) {
        	touchX = joystickOffset - (joystickSize / 3) + 5;
        } else {
        	touchX = x - joystickOffset;
        }
        
        if(y > joystickOffset + joystickSize - 20) {
            touchY = joystickOffset + (joystickSize / 2) + 20;
        } else if(y < joystickOffset + 10) {
        	touchY = joystickOffset - (joystickSize / 3) + 10;
        } else {
        	touchY = y - joystickOffset;
        }
        
        calculatePower(touchX, touchY);
    }
    
    public void reset() {
		isActive = false;
        touchX = joystickSize / 2 - 15;
        touchY = joystickSize / 2;
        powerX = 0;
        powerY = 0;
    }
    
    private void calculatePower(int x, int y) {
        powerX = 0;
        powerY = 0;
        
        if(x > 97 + 10) {
            powerX = x > (97 + 50) ? 4 : 2;
        } else if(x < 97 - 10) {
        	powerX = x < (97 - 50) ? -4 : -2;
        }
        
        if(y > 112 + 10) {
            powerY = y > 112 + 50 ? 4 : 2;
        } else if(y < 112 - 10) {
        	powerY = y < 112 - 50 ? -4 : -2;
        }
    }
    
    public Vector2 getPower() {
        return new Vector2(powerX * 10, powerY * 10);
    }
	
	public void setPosition(Vector2 vector) {
		this.position = vector;
	}
    
    public String getDebugValues() {
        String result = "PowerX: " + powerX;
        result += "\nPowerY: " + powerY;
        result += "\nJoystickTouchX: " + touchX;
        result += "\nJoystickTouchY: " + touchY;
        return result;
    }
    
    public void dispose() {
        for(Texture texture : joystickTextures) {
            texture.dispose();
        }
    }
}
