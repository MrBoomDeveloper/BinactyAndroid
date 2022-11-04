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
        rectangels.put("test", rect);
        textures.put("test", new Texture(Gdx.files.internal("img/terrain/test.jpg")));
    }
    
    public void renderTextures() {
        Rectangle rect = rectangels.get("test");
        sprites.draw(textures.get("test"), rect.x, rect.y);
    }
}