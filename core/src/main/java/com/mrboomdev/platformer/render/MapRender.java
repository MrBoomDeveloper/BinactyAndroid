package com.mrboomdev.platformer.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.HashMap;

public class MapRender {
    private SpriteBatch sprites;
    private HashMap<String, Texture> textures = new HashMap<String, Texture>();
    private HashMap<String, Rectangle> rectangels = new HashMap<String, Rectangle>();
    
    public MapRender(SpriteBatch sprites) {
        this.sprites = sprites;
        loadTextures();
    }
    
    private void loadTextures() {
        Rectangle rect = new Rectangle();
        rect.x = 800 / 2 - 64 / 2;
        rect.y = 20;
        rect.width = 64;
        rect.height = 64;
        rectangels.put("floor", rect);
        textures.put("floor", new Texture(Gdx.files.internal("img/terrain/floor.jpg")));
    }
    
    public void renderTextures() {
        for(int x = 0; x < 10; x++) {
            for(int y = 0; y < 10; y++) {
        		Rectangle rect = rectangels.get("floor");
        		sprites.draw(textures.get("floor"), x*64, y*64);
            }
        }
    }
}