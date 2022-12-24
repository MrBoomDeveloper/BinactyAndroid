package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.graphics.Camera;
import com.google.gson.reflect.TypeToken;
import com.mrboomdev.platformer.util.SizeUtil.Bounds;
import com.mrboomdev.platformer.environment.Block;
import com.mrboomdev.platformer.environment.MapData;
import java.lang.reflect.Type;
import java.util.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.Gson;

public class MapManager {
    private Camera camera;
    private MapBuilder builder;
    private MapData data;
    private Bounds cameraBounds;
    private Vector2 cameraStart, cameraEnd;
    private String json;
    
    public MapManager() {
        cameraEnd = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraStart = new Vector2(0, 0);
    }
    
    public void load(FileHandle file) {
        Gson gson = new Gson();
        json = file.readString();
        data = gson.fromJson(json, MapData.class);
        builder = new MapBuilder(data.tiles);
    }
    
    public void build(World world) {
        Type token = new TypeToken<Map<String, Block>>(){}.getType();
        Map<String, Block> blocks = new Gson().fromJson(Gdx.files.internal("world/blocks.json").readString(), token);
        
        builder.loadBlocks(data.load, blocks);
        builder.build(world);
    }
	
	public void render(SpriteBatch batch, MapLayer layer) {
        updateCameraBounds();
        builder.render(layer, batch, cameraBounds);
	}
    
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    public void updateCameraBounds() {
        cameraBounds = new Bounds(
            camera.position.x - (camera.viewportWidth / 2),
            camera.position.y - (camera.viewportHeight / 2),
            camera.position.x + (camera.viewportWidth / 2),
            camera.position.y + (camera.viewportHeight / 2)
        );
    }
}