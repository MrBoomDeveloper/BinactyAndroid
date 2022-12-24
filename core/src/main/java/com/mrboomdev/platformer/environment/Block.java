package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mrboomdev.platformer.util.SizeUtil.Bounds;

public class Block {
    public float width, height;
    public float offsetX = 0, offsetY = 0;
    public String texture;
    public boolean colission = true;
    public Sprite sprite;
    public Bounds bounds;
    
    public Block init() {
        if(texture == null) return this;
        try {
            sprite = new Sprite(new Texture(Gdx.files.internal("world/blocks/" + texture)));
        } catch(Exception e) {
            sprite = new Sprite(new Texture(Gdx.files.internal("world/blocks/error.png")));
            e.printStackTrace();
        }
        sprite.setSize(width, height);
        return this;
    }
}