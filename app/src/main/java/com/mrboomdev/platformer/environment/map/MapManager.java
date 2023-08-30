package com.mrboomdev.platformer.environment.map;

import androidx.annotation.NonNull;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.game.pack.PackLoader;
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.LogUtil;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import box2dLight.RayHandler;

@SuppressWarnings("UnusedDeclaration")
public class MapManager {
	public Atmosphere atmosphere;
	private List<MapTile> tiles;
	@Json(ignore = true)
	private long lastSortedTime;
	@Json(ignore = true)
	public RayHandler rayHandler;
	@Json(ignore = true)
	public Map<String, MapTile> tilesPresets = new HashMap<>();
	@Json(ignore = true)
	public ObjectMap<String, MapTile> tilesMap = new OrderedMap<>();
	@Json(ignore = true)
	public Array<MapObject> pendingRemoves = new Array<>();
	@Json(ignore = true)
	public ArrayList<MapObject> objects = new ArrayList<>();
	@Json(ignore = true)
	private FileUtil source;
	@Json(ignore = true)
	private World world;
	@Json(ignore = true)
	private Runnable buildCallback;
	@Json(ignore = true)
	private Status status = Status.PREPARING;
	@Json(ignore = true)
	private GameHolder game = GameHolder.getInstance();
	
	public void render(SpriteBatch batch) {
		if(!pendingRemoves.isEmpty()) {
			pendingRemoves.forEach(MapObject::remove);
			pendingRemoves.clear();
		}

		try {
			Collections.sort(objects);
		} catch(Exception e) {
			e.printStackTrace();
		}

		for(MapObject object : objects) {
			object.draw(batch);
		}
	}
	
	public MapManager build(World world, FileUtil source, Runnable callback) {
		try {
			Moshi moshi = new Moshi.Builder().add(new MapTile.Adapter()).build();
			JsonAdapter<PackData.Tiles> adapter = moshi.adapter(PackData.Tiles.class);
			this.world = world;
			this.source = source;
			this.buildCallback = callback;
		
			for(String pack : atmosphere.tiles) {
				var tilesFile = PackLoader.resolvePath(source.getParent(), pack);
				var tilesPreset = Objects.requireNonNull(adapter.fromJson(tilesFile.readString(true))).tiles;
				tilesPreset.values().forEach(tile -> tile.source = tilesFile.getParent());

				if(pack.startsWith("$")) {
					var packName = pack.substring(1, pack.indexOf("/"));
					addPrefix(tilesPreset, packName + ":");
				}

				tilesPresets.putAll(tilesPreset);
			}
		
			for(MapTile tile : tilesPresets.values()) {
				if(tile.devTexture != null && game.settings.enableEditor && !tile.source.goTo(tile.devTexture).isAddedToAsyncLoading()) {
					tile.source.goTo(tile.devTexture).loadAsync(Texture.class);
				}
				if(tile.texture == null || tile.source.goTo(tile.texture).isAddedToAsyncLoading()) continue;
				tile.source.goTo(tile.texture).loadAsync(Texture.class);
			}
			status = Status.LOADING_RESOURCES;
		} catch(Exception e) {
			LogUtil.crash("Failed to build a map.", "Something went wrong while building a map of the environment.", e);
			e.printStackTrace();
		}
		return this;
	}
	
	private void addPrefix(@NonNull Map<String, MapTile> hashMap, String prefix) {
    	Map<String, MapTile> newHashMap = new HashMap<>();
		for(var entry : hashMap.entrySet()) {
        	String newKey = prefix + entry.getKey();
        	MapTile value = entry.getValue();
        	newHashMap.put(newKey, value);
    	}
   	 hashMap.clear();
   	 hashMap.putAll(newHashMap);
	}

	
	//Prevents from duplicate tiles
	@NonNull
	private String getTextPosition(@NonNull float[] position, int layer) {
		return Math.round(position[0]) + ":" + Math.round(position[1]) + ":" + layer;
	}
	
	public void addTile(@NonNull String name, float[] position, int layer) {
		if(name.equals("ERASER")) {
			removeTile(position, layer);
			return;
		} else if(name.equals("SELECT")) {
			var pos = getTextPosition(position, layer);
			game.environment.ui.editor.selectTile(tilesMap.containsKey(pos) ? tilesMap.get(pos) : null);
			return;
		}
		
		var preset = tilesPresets.get(name);
		var pos = getTextPosition(position, layer);
		if(tilesMap.containsKey(pos)) return;
		
		MapTile tile = new MapTile();
		if(preset == null) {
			throw BoomException.builder("Tile preset was not found: ").addQuoted(name).build();
		}
		tile.copyData(preset);
		position[0] = Math.round(position[0]);
		position[1] = Math.round(position[1]);
		
		tile.position = position;
		tile.name = name;
		tile.layer = layer;
		tile.build(world);
		if(rayHandler != null) tile.setupRayHandler(rayHandler);
		
		objects.add(tile);
		tiles.add(tile);
		tilesMap.put(pos, tile);
	}
	
	public void removeTile(float[] position, int layer) {
		var pos = getTextPosition(position, layer);
		if(!tilesMap.containsKey(pos)) return;
		
		var removed = tilesMap.get(pos);
		pendingRemoves.add(removed);
		objects.remove(removed);
		tiles.remove(removed);
		tilesMap.remove(pos);
	}
	
	public void ping() {
		if(game.assets.update(17) && game.externalAssets.update(17) && status == Status.LOADING_RESOURCES) {
			buildTerrain();
			status = Status.BUILDING_BLOCKS;
		}
	}
	
	private void buildTerrain() {
		for(var tile : tilesPresets.values()) {
			if(tile.devTexture != null && game.settings.enableEditor) {
				tile.setTexture(tile.source.goTo(tile.devTexture).getLoaded(Texture.class), true);
			}
			if(tile.texture != null) {
				tile.setTexture(tile.source.goTo(tile.texture).getLoaded(Texture.class), false);
			}
		}
		
		for(var tile : tiles) {
			tile.copyData(Objects.requireNonNull(tilesPresets.get(tile.name)));
			tile.build(world);
			objects.add(tile);
			tilesMap.put(getTextPosition(tile.position, tile.layer), tile);
		}
		
		status = Status.DONE;
		buildCallback.run();
	}
	
	public static class Atmosphere {
		public ColorUtil environmentLightColor, playerLightColor;
		public String[] tiles;
	}
	
	private enum Status {
		PREPARING,
		LOADING_RESOURCES,
		BUILDING_BLOCKS,
		DONE
	}
}