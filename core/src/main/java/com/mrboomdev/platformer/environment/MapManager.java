package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.Gson;

public class MapManager {
    private MapBuilder builder;
    private MapData data;
    private String json;
    private Vector2 size;
    
    public MapManager() {
        size = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    public void load(FileHandle file) {
        Gson gson = new Gson();
        json = file.readString();
        data = gson.fromJson(json, MapData.class);
        builder = new MapBuilder(data.tiles);
        builder.loadTextures(data.load);
    }
    
    public void build(World world) {
        builder.build(world);
    }
	
	public void render(SpriteBatch batch, MapLayer layer) {
        builder.render(batch, size);
	}
    
    public void setSceneSize(Vector2 size) {
        this.size = size;
    }
}