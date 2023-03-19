package com.mrboomdev.platformer.environment.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.gameplay.GameplayScreen;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.util.FileUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class MapManager {
	@Expose public Atmosphere atmosphere;
	@Expose public Rules rules;
	@Expose private ArrayList<MapTile> tiles = new ArrayList<>();
	public RayHandler rayHandler;
	public HashMap<String, MapTile> tilesPresets = new HashMap<>();
	public ObjectMap<String, MapTile> tilesMap = new ObjectMap<>();
	private LoadingFiles loadFiles;
	private FileUtil source;
	private World world;
	private Runnable buildCallback;
	private Status status = Status.PREPAIRING;
	private GameHolder game = GameHolder.getInstance();
	public Array<MapObject> pendingRemoves = new Array<>();
	public ArrayList<MapObject> objects = new ArrayList<>();
	
	public void render(SpriteBatch batch) {
		pendingRemoves.forEach(obj -> obj.remove());
		pendingRemoves.clear();
		Collections.sort(objects);
		for(MapObject object : objects) {
			object.draw(batch);
		}
	}
	
	public MapManager build(World world, FileUtil source, Runnable callback) {
		Gson gson = new Gson();
		this.world = world;
		this.source = source;
		this.buildCallback = callback;
		
		for(String pack : atmosphere.tiles) {
			var type = new TypeToken<HashMap<String, MapTile>>(){}.getType();
			FileUtil path = new FileUtil(pack, FileUtil.Source.INTERNAL);
			HashMap<String, MapTile> tilesPreset = null;
			if(pack.startsWith("$")) {
				path = new FileUtil("packs/" + pack.substring(1, pack.length()), FileUtil.Source.INTERNAL);
				tilesPresets = gson.fromJson(Gdx.files.internal(path.getPath()).readString(), type);
			} else {
				tilesPreset = gson.fromJson(source.getParent().goTo(path.getPath()).readString(true), type);
			}
			for(var tile : tilesPreset.entrySet()) {
				tile.getValue().source = path;
			}
			this.tilesPresets.putAll(tilesPreset);
		}
		
		ArrayList<LoadingFiles.File> files = new ArrayList<>();
		for(MapTile tile : tilesPresets.values()) {
			if(tile.devTexturePath != null && game.settings.enableEditor) {
				files.add(new LoadingFiles.File(source.getParent().getParent().goTo(tile.devTexturePath).getPath(), "texture"));
			}
			if(tile.texturePath == null) continue;
			files.add(new LoadingFiles.File(source.getParent().getParent().goTo(tile.texturePath).getPath(), "texture"));
		}
		Gdx.app.postRunnable(() -> {
			LoadingFiles.loadToManager(files, "", game.assets);
			status = Status.LOADING_RESOURCES;
		});
		return this;
	}
	
	private String getTextPosition(float[] position, int layer, boolean round) {
		return Math.round(position[0]) + ":" + Math.round(position[1]) + ":" + layer;
	}
	
	private float[] getCorrectPosition(float[] position, MapTile tile) {
		//position[1] += tile.size[1] / 2 - .4f;
		return position;
	}
	
	public void addTile(String name, float[] position, int layer) {
		if(name == "ERASER") {
			removeTile(position, layer);
			return;
		}
		var preset = tilesPresets.get(name);
		var correctPosition = getCorrectPosition(position, preset);
		
		var pos = getTextPosition(correctPosition, layer, true);
		if(tilesMap.containsKey(pos)) return;
		
		MapTile tile = new MapTile();
		tile.copyData(preset);
		correctPosition[0] = Math.round(correctPosition[0]);
		correctPosition[1] = Math.round(correctPosition[1]);
		
		tile.position = correctPosition;
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
			if(tile.devTexturePath != null && game.settings.enableEditor) {
				tile.setTexture(game.assets.get(source.getParent().getParent().goTo(tile.devTexturePath).getPath()), true);
			}
			if(tile.texturePath != null) {
				tile.setTexture(game.assets.get(source.getParent().getParent().goTo(tile.texturePath).getPath()), false);
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
	
	public class Atmosphere {
		@Expose public ColorUtil color;
		@Expose public String[] tiles;
	}
	
	public class Rules {
		@Expose public float[] worldBorder = {-1000, -1000, 1000, 1000};
		@Expose public float[] cameraBorder = {-1000, -1000, 1000, 1000};
	}
	
	private enum Status {
		PREPAIRING,
		LOADING_RESOURCES,
		BUILDING_BLOCKS,
		DONE
	}
}