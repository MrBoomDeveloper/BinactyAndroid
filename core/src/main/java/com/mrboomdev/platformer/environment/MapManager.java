package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.object.Block;

public class MapManager {
	private SpriteBatch batch;
	private Array<Block> blocks = new Array<Block>();
	public boolean isLoaded = false;
	
	public MapManager(SpriteBatch batch) {
		this.batch = batch;
	}
	
	public void load(String name) {
		String json = "[{'name': 'floor', 'x': 0, 'y': 0}]";
		//TODO: PLEASE PARSE THIS JSON
		//Array<String> q = JSON.parse
		this.isLoaded = true;
	}
	
	public void render() {
		if(this.isLoaded) {
			blocks.forEach((Block block) -> {
				batch.draw(block.texture, block.position.x, block.position.y);
			});
		}
	}
}