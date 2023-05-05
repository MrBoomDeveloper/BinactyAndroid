package com.mrboomdev.platformer.entity.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.entity.Entity;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Json;

public class Item {
	public String name;
	public Stats stats;
	public Flags flags;
	public Entity.Frame skin;
	public Entity.States state;
	@Json(ignore = true) public FileUtil source;
	@Json(ignore = true) Sprite sprite;
	
	public Sprite getSprite(Vector2 position) {
		if(sprite == null) {
			sprite = new Sprite(new Texture(source.goTo(skin.texture).getFileHandle()));
			sprite.setSize(skin.size[0], skin.size[1]);
		}
		sprite.setPosition(position.x + skin.position[0], position.y + skin.position[1]);
		return sprite;
	}
	
	public Item cpy() {
		var copy = new Item();
		copy.name = name;
		copy.stats = stats;
		copy.flags = flags;
		copy.source = source;
		copy.state = state;
		copy.skin = skin;
		return copy;
	}
	
	public static class Stats {
		@Json(name = "max_count") public int maxCount;
		@Json(name = "reload_duration") public int reloadDuration;
	}
	
	public static class Flags {
		@Json(name = "is_togglable") public boolean isTogglable;
		@Json(name = "is_aimable") public boolean isAimable;
	}
}