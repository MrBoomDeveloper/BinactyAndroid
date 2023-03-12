package com.mrboomdev.platformer.environment.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.util.FileUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class MapManager {
	public Atmosphere atmosphere;
	public Rules rules;
	public RayHandler rayHandler;
	private HashMap<String, MapTile> tilesPresets = new HashMap<>();
	private LoadingFiles loadFiles;
	private FileUtil source;
	private World world;
	private Runnable buildCallback;
	private AssetManager assets;
	private Status status = Status.PREPAIRING;
	public ArrayList<MapObject> objects = new ArrayList<>();
	private ArrayList<MapTile> tiles = new ArrayList<>();
	
	public void render(SpriteBatch batch) {
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
		this.assets = GameHolder.getInstance().assets;
		
		for(String pack : atmosphere.tiles) {
			var type = new TypeToken<HashMap<String, MapTile>>(){}.getType();
			tilesPresets.putAll(gson.fromJson(source.getParent().goTo(pack).readString(), type));
		}
		
		ArrayList<LoadingFiles.File> files = new ArrayList<>();
		for(MapTile tile : tilesPresets.values()) {
			files.add(new LoadingFiles.File(source.getParent().getParent().goTo(tile.texturePath).getPath(), "texture"));
		}
		Gdx.app.postRunnable(() -> {
			LoadingFiles.loadToManager(files, "", assets);
			status = Status.LOADING_RESOURCES;
		});
		return this;
	}
	
	public void addTile(String name, float[] position, int layer) {
		MapTile tile = new MapTile();
		//tile.block = blocks.get(name).cpy();
		position[0] = Math.round(position[0]);
		position[1] = Math.round(position[1]);
		//tile.position = position;
		//tile.layer = layer;
		//tile.build(world);
		//tiles.add(tile);
	}
	
	public void ping() {
		if(assets.update(17) && status == Status.LOADING_RESOURCES) {
			buildTerrain();
			status = Status.BUILDING_BLOCKS;
		}
	}
	
	private void buildTerrain() {
		for(var tile : tilesPresets.values()) {
			tile.setTexture(assets.get(source.getParent().getParent().goTo(tile.texturePath).getPath()));
		}
		
		for(var tile : tiles) {
			tile.copyData(tilesPresets.get(tile.name));
			tile.build(world);
			objects.add(tile);
		}
		
		status = Status.DONE;
		buildCallback.run();
	}
	
	public class Atmosphere {
		public ColorUtil color;
		public String[] tiles;
	}
	
	public class Rules {
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