package com.mrboomdev.platformer.environment.map.tile;

import com.badlogic.gdx.graphics.g2d.Animation;
import java.util.List;
import java.util.Map;

public class TileStyle {
	public String initial, current;
	public List<String> queue;
	public Map<String, Style> types;
	
	public TileStyle getSerialized() {
		var style = new TileStyle();
		style.initial = initial;
		return style;
	}
	
	public TileStyle build() {
		current = initial == null ? "default" : initial;
		return this;
	}
	
	public void selectStyle(String name) {
		
	}
	
	public static class Style {
		public int[] region;
		public float[] size;
		public Frame[] frames;
		public float speed;
		public Animation.PlayMode mode;
	}
	
	public static class Frame {
		public int[] region;
	}
}