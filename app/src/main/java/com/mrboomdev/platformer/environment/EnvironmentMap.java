package com.mrboomdev.platformer.environment;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrboomdev.platformer.util.ColorUtil;
import com.mrboomdev.platformer.util.FileUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class EnvironmentMap {
	public Atmosphere atmosphere;
	public Rules rules;
	public RayHandler rayHandler;
	public ArrayList<Tile> tiles = new ArrayList<>();
	private HashMap<String, EnvironmentBlock> blocks = new HashMap<>();
	private FileUtil source;
	private World world;
	
	public void render(SpriteBatch batch) {
		Collections.sort(tiles);
		for(Tile tile : tiles) {
			tile.draw(batch);
		}
	}
	
	public EnvironmentMap build(World world, FileUtil source) {
		this.world = world;
		this.source = source;
		
		Gson gson = new Gson();
		TypeToken<HashMap<String, EnvironmentBlock>> type = new TypeToken<>(){};
		for(String pack : atmosphere.tiles) {
			blocks = gson.fromJson(Gdx.files.internal(source.concatPath(pack)).readString(), type);
			blocks.values().forEach(block -> block.init(source.getParentPath()));
		}
		for(Tile tile : tiles) {
			tile.block = blocks.get(tile.name).cpy();
			tile.build(world);
		}
		return this;
	}
	
	public void addTile(String name, float[] position, int layer) {
		Tile tile = new Tile();
		tile.block = blocks.get(name).cpy();
		position[0] = (int)(position[0]);
		position[1] = (int)(position[1]);
		tile.position = position;
		tile.layer = layer;
		tile.build(world);
		tiles.add(tile);
	}
	
	public class Tile implements Comparable<Tile> {
		public String name, style;
		public int layer;
		public float[] position;
		public EnvironmentBlock block;
		
		public void draw(SpriteBatch batch) {
			block.draw(batch);
		}
		
		public void build(World world) {
			block.build(world, new Vector2(position[0], position[1]));
		}
		
		public Vector3 getPosition() {
			return new Vector3(position);
		}
		
		@Override
		public int compareTo(Tile tile) {
			if(layer != tile.layer) {
				return layer - tile.layer;
			}
			if(position[1] != tile.position[1]) {
				return Math.round(tile.getPosition().y - getPosition().y);
			}
			return Math.round(getPosition().x - tile.getPosition().x);
		}
	}
	
	public class Atmosphere {
		public ColorUtil color;
		public String[] music;
		public String[] tiles;
	}
	
	public class Rules {
		public float[] worldBorder = {-1000, -1000, 1000, 1000};
		public float[] cameraBorder = {-1000, -1000, 1000, 1000};
	}
}