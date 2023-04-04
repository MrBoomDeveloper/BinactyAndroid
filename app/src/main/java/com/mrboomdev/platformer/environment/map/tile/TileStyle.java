package com.mrboomdev.platformer.environment.map.tile;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.squareup.moshi.Json;
import java.util.List;
import java.util.Map;

public class TileStyle {
	public String initial;
	public List<String> queue;
	public Map<String, Style> types;
	@Json(ignore = true) public Style current;
	@Json(ignore = true) public int currentId;
	@Json(ignore = true) Sprite atlas;
	
	public TileStyle() {}
	
	public TileStyle(TileStyle style) {
		this.queue = style.queue;
		this.types = style.types;
	}
	
	public Sprite getSprite(Vector2 position, MapTile tile) {
		var sprite = current.getSprite(atlas, tile);
		sprite.setCenter(position.x, position.y);
		return sprite;
	}
	
	public TileStyle getSerialized() {
		var style = new TileStyle();
		for(var type : types.entrySet()) {
			if(type.getValue() == current) style.initial = type.getKey();
		}
		return style;
	}
	
	public TileStyle build(Sprite sprite) {
		this.atlas = new Sprite(sprite);
		selectStyle(initial == null ? (types.containsKey("default") ? "default" : (String)types.keySet().toArray()[0]) : initial);
		return this;
	}
	
	public void selectStyle(String name) {
		current = types.get(name);
	}
	
	public void clone(TileStyle style) {
		this.queue = style.queue;
		this.types = style.types;
	}
	
	public static class Style {
		public int[] region;
		public float[] size;
		public Frame[] frames;
		public float speed;
		public Animation.PlayMode mode;

		public Sprite getSprite(Sprite sprite, MapTile tile) {
			if(size == null) size = tile.size;
			var result = new Sprite(sprite, region[0], region[1], region[2], region[3]);
			result.setSize(size[0], size[1]);
			return result;
		}
	}
	
	public static class Frame {
		public int[] region;
	}
}