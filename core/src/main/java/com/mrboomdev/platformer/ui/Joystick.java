package com.mrboomdev.platformer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.ui.Joystick;

public class Joystick {
    private SpriteBatch sprites;
    private Array<Texture> joystickTextures = new Array<Texture>();
    private final int joystickOffset = 75;
    private final int joystickSize = 225;
    private int touchX = 0;
    private int touchY = 0;
    
    public Joystick(SpriteBatch sprites) {
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
        y = y * -1 + 720;
        
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
    }
    
    public void reset() {
        touchX = joystickSize / 2 - 15;
        touchY = joystickSize / 2;
    }
}
