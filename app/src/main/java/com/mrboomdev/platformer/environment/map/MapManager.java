package com.mrboomdev.platformer.environment.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.platformer.environment.pack.PackMap;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.util.FileUtil;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MapManager {
	public Atmosphere atmosphere;
	public Rules rules;
	private List<MapTile> tiles;
	@Json(ignore = true) public RayHandler rayHandler;
	@Json(ignore = true) public Map<String, MapTile> tilesPresets = new HashMap<>();
	@Json(ignore = true) public ObjectMap<String, MapTile> tilesMap = new ObjectMap<>();
	@Json(ignore = true) private LoadingFiles loadFiles;
	@Json(ignore = true) private FileUtil source;
	@Json(ignore = true) private World world;
	@Json(ignore = true) private Runnable buildCallback;
	@Json(ignore = true) private Status status = Status.PREPAIRING;
	@Json(ignore = true) private GameHolder game = GameHolder.getInstance();
	@Json(ignore = true) public Array<MapObject> pendingRemoves = new Array<>();
	@Json(ignore = true) public ArrayList<MapObject> objects = new ArrayList<>();
	
	public void render(SpriteBatch batch) {
		pendingRemoves.forEach(obj -> obj.remove());
		pendingRemoves.clear();
		Collections.sort(objects);
		for(MapObject object : objects) {
			object.draw(batch);
		}
	}
	
	public MapManager build(World world, FileUtil source, Runnable callback) {
		try {
			Moshi moshi = new Moshi.Builder().build();
			JsonAdapter<PackMap.Tiles> adapter = moshi.adapter(PackMap.Tiles.class);
			this.world = world;
			this.source = source;
			this.buildCallback = callback;
		
			for(String pack : atmosphere.tiles) {
				FileUtil path = new FileUtil(pack, FileUtil.Source.INTERNAL);
				Map<String, MapTile> tilesPreset = null;
				if(pack.startsWith("$")) {
					path = new FileUtil("packs/" + pack.substring(1, pack.length()), FileUtil.Source.INTERNAL);
					tilesPreset = adapter.fromJson(Gdx.files.internal(path.getPath()).readString()).tiles;
				} else {
					tilesPreset = adapter.fromJson(source.getParent().goTo(path.getPath()).readString(true)).tiles;
				}
				for(var tile : tilesPreset.entrySet()) {
					tile.getValue().source = path;
				}
				tilesPresets.putAll(tilesPreset);
			}
		
			ArrayList<LoadingFiles.File> files = new ArrayList<>();
			for(MapTile tile : tilesPresets.values()) {
				if(tile.devTexture != null && game.settings.enableEditor) {
					files.add(new LoadingFiles.File(source.getParent().getParent().goTo(tile.devTexture).getPath(), "texture"));
				}
				if(tile.texture == null) continue;
				files.add(new LoadingFiles.File(source.getParent().getParent().goTo(tile.texture).getPath(), "texture"));
			}
			Gdx.app.postRunnable(() -> {
				LoadingFiles.loadToManager(files, "", game.assets);
				status = Status.LOADING_RESOURCES;
			});
		} catch(Exception e) {
			Gdx.files.external("crash.txt").writeString("Crashed while building the map.\n" + e.getMessage(), false);
			e.printStackTrace();
			game.launcher.exit(GameLauncher.Status.CRASH);
		}
		return this;
	}
	
	private String getTextPosition(float[] position, int layer, boolean round) {
		return Math.round(position[0]) + ":" + Math.round(position[1]) + ":" + layer;
	}
	
	public void addTile(String name, float[] position, int layer) {
		if(name == "ERASER") {
			removeTile(position, layer);
			return;
		} else if(name == "SELECT") {
			var pos = getTextPosition(position, layer, true);
			game.environment.ui.editor.selectTile(tilesMap.containsKey(pos) ? tilesMap.get(pos) : null);
			return;
		}
		
		var preset = tilesPresets.get(name);
		var pos = getTextPosition(position, layer, true);
		if(tilesMap.containsKey(pos)) return;
		
		MapTile tile = new MapTile();
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
		var pos = getTextPosition(position, layer, true);
		if(!tilesMap.containsKey(pos)) return;
		
		var removed = tilesMap.get(pos);
		pendingRemoves.add(removed);
		objects.remove(objects.indexOf(removed));
		tiles.remove(tiles.indexOf(removed));
		tilesMap.remove(pos);
	}
	
	public void ping() {
		if(game.assets.update(17) && status == Status.LOADING_RESOURCES) {
			buildTerrain();
			status = Status.BUILDING_BLOCKS;
		}
	}
	
	private void buildTerrain() {
		for(var tile : tilesPresets.values()) {
			if(tile.devTexture != null && game.settings.enableEditor) {
				tile.setTexture(game.assets.get(source.getParent().getParent().goTo(tile.devTexture).getPath()), true);
			}
			if(tile.texture != null) {
				tile.setTexture(game.assets.get(source.getParent().getParent().goTo(tile.texture).getPath()), false);
			}
		}
		
		for(var tile : tiles) {
			tile.copyData(tilesPresets.get(tile.name));
			tile.build(world);
			objects.add(tile);
			tilesMap.put(getTextPosition(tile.position, tile.layer, true), tile);
		}
		
		status = Status.DONE;
		buildCallback.run();
	}
	
	public static class Atmosphere {
		public ColorUtil environmentLightColor, playerLightColor;
		public String[] tiles;
	}
	
	public static class Rules {
		public float[] worldBorder = {-1000, -1000, 1000, 1000};
		public float[] cameraBorder = {-1000, -1000, 1000, 1000};
	}
	
	private enum Status {
		PREPAIRING,
		LOADING_RESOURCES,
		BUILDING_BLOCKS,
		DONE
	}
}