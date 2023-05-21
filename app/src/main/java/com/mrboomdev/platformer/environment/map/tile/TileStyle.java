package com.mrboomdev.platformer.environment.map.tile;

import androidx.annotation.NonNull;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
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
	@Json(ignore = true) public MapTile owner;
	@Json(ignore = true) String lastWas = "";
	@Json(ignore = true) Sprite atlas;
	
	public TileStyle() {}
	
	public TileStyle(@NonNull TileStyle style) {
		this.queue = style.queue;
		this.types = style.types;
	}
	
	public Sprite getSprite(Vector2 position, MapTile tile) {
		if(current.collision != null && !lastWas.equals(current.id)) {
			((PolygonShape)owner.fixture.getShape()).setAsBox(
				current.collision[0] / 2, current.collision[1] / 2,
				new Vector2(current.collision[2] / 2, current.collision[3] / 2), 0);
		}
		if(current.shadow_collision != null && !lastWas.equals(current.id)) {
			((PolygonShape)owner.shadowFixture.getShape()).setAsBox(
				current.shadow_collision[0] / 2, current.shadow_collision[1] / 2, new Vector2(
				current.shadow_collision[2] / 2 * (owner.flipX ? -1 : 1),
				current.shadow_collision[3] / 2 * (owner.flipY ? -1 : 1)), 0);
		}
		
		var sprite = current.getSprite(atlas, tile);
		sprite.setCenter(position.x, position.y);
		lastWas = current.id;
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
		for(var entry : types.entrySet()) {
			entry.getValue().id = entry.getKey();
		}
		selectStyle(initial == null ? (types.containsKey("default") ? "default" : (String)types.keySet().toArray()[0]) : initial);
		return this;
	}
	
	public void selectStyle(String name) {
		//if(!types.containsKey(name)) return;
		current = types.get(name);
	}
	
	public void clone(TileStyle style) {
		this.queue = style.queue;
		this.types = style.types;
	}
	
	public static class Style {
		public int[] region;
		public float[] size, collision, shadow_collision;
		public Frame[] frames;
		public float speed;
		@Json(ignore = true) public String id;

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