package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Block {
    public float width, height;
    public String texture;
    public boolean colission;
    public Sprite sprite;
    
    public Block init() {
        if(texture == null) return this;
        sprite = new Sprite(new Texture(Gdx.files.internal("img/tiles/" + texture)));
        sprite.setSize(width, height);
        return this;
    }
}