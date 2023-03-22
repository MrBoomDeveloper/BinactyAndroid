package com.mrboomdev.platformer.environment;

import box2dLight.RayHandler;
import com.badlogic.gdx.utils.Array;
import com.google.gson.reflect.TypeToken;
import com.mrboomdev.platformer.entity.EntityManager.Spawn;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.lang.reflect.Type;
import java.util.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.Gson;

@Deprecated
public class MapManager {
    private MapBuilder builder;
    private MapData data;
    public Array<Spawn> spawnPositions = new Array<>();
    
	@Deprecated
    public void load(FileHandle file) {
		try {
			Moshi moshi = new Moshi.Builder().build();
			JsonAdapter<MapData> adapter = moshi.adapter(MapData.class);
     	   data = adapter.fromJson(file.readString());
   	     builder = new MapBuilder(data.tiles, this);
		} catch(Exception e) {
			Gdx.files.external("crash.txt").writeString("Crashed while loading the old map.\n" + e.getMessage(), false);
			e.printStackTrace();
		}
    }
    
	@Deprecated
    public void build(World world, RayHandler rayHandler) {
		try {
			Moshi moshi = new Moshi.Builder().build();
			JsonAdapter<Map<String, Block>> adapter = moshi.adapter(new TypeToken<Map<String, Block>>(){}.getType());
			Map<String, Block> blocks = adapter.fromJson(Gdx.files.internal("world/blocks.json").readString());
     	   builder.loadBlocks(data.load, blocks);
   	     builder.build(world, rayHandler);
		} catch(Exception e) {
			Gdx.files.external("crash.txt").writeString("Crashed while building the old map.\n" + e.getMessage(), false);
			e.printStackTrace();
		}
    }
}