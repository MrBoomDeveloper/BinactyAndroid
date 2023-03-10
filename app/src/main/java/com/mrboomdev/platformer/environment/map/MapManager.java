package com.mrboomdev.platformer.environment.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.mrboomdev.platformer.environment.EnvironmentBlock;
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
	
	@SerializedName("tiles")
	public ArrayList<MapObject<MapObject>> objects = new ArrayList<>();
	
	public void render(SpriteBatch batch) {
		Collections.sort(objects);
		for(MapObject tile : objects) {
			tile.draw(batch);
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
			tilesPresets.putAll(gson.fromJson(source.goTo(pack).readString(), type));
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
		position[0] = (int)(position[0]);
		position[1] = (int)(position[1]);
		//tile.position = position;
		//tile.layer = layer;
		//tile.build(world);
		//tiles.add(tile);
	}
	
	public void ping() {
		if(status == Status.LOADING_RESOURCES && assets.update(17)) {
			buildTerrain();
			status = Status.BUILDING_BLOCKS;
		}
	}
	
	private void buildTerrain() {
		for(var tile : tilesPresets.values()) {
			//tile.setTexture(assets.get(source.getParent().getParent().goTo(tile.texturePath).getPath()));
		}
		
		for(var object : objects) {
			var tile = (MapTile)object;
			//tile.block = blocks.get(tile.name).cpy();
			//tile.build(world);
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