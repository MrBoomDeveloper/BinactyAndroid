package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.mrboomdev.platformer.environment.MapBlock;
import com.mrboomdev.platformer.environment.MapLayer;

public class MapManager {
	private SpriteBatch batch;
	private Array<MapBlock> blocks = new Array<MapBlock>();
	
	public MapManager(SpriteBatch batch) {
		this.batch = batch;
	}
	
	public void load(String name) {
		String json = "[{'name': 'floor', 'x': 0, 'y': 0}]";
		//TODO: PARSE THIS JSON
		//Array<String> q = JSON.parse
	}
	
	public void render(SpriteBatch batch, MapLayer layer) {
		/*blocks.forEach((Block block) -> {
			batch.draw(block.texture, block.position.x, block.position.y);
		});*/
	}
}