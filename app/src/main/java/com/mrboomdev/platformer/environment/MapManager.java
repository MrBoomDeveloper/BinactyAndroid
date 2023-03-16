package com.mrboomdev.platformer.environment;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.google.gson.reflect.TypeToken;
import com.mrboomdev.platformer.entity.EntityManager.Spawn;
import java.lang.reflect.Type;
import java.util.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.Gson;

@Deprecated
public class MapManager {
    private Camera camera;
    private MapBuilder builder;
    private MapData data;
    private Vector2 cameraStart, cameraEnd;
    public Array<Spawn> spawnPositions = new Array<>();
    
    public MapManager() {
        cameraEnd = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraStart = new Vector2(0, 0);
    }
    
	@Deprecated
    public void load(FileHandle file) {
        Gson gson = new Gson();
        String json = file.readString();
        data = gson.fromJson(json, MapData.class);
        builder = new MapBuilder(data.tiles, this);
    }
    
	@Deprecated
    public void build(World world, RayHandler rayHandler) {
        Type token = new TypeToken<Map<String, Block>>(){}.getType();
        Map<String, Block> blocks = new Gson().fromJson(Gdx.files.internal("world/blocks.json").readString(), token);
        builder.loadBlocks(data.load, blocks);
        builder.build(world, rayHandler);
    }
	
	@Deprecated
	public void render(SpriteBatch batch) {
        builder.render(batch);
	}
    
	@Deprecated
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}