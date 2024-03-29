package com.mrboomdev.binacty.script.bridge;

import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.game.GameHolder;

public class MapBridge {
	private final GameHolder game = GameHolder.getInstance();
	
	public MapTile getById(String id) {
		for(var tile : game.environment.map.tilesMap.values()) {
			if(tile.id != null && tile.id.equals(id)) {
				return tile;
			}
		}

		return null;
	}
}