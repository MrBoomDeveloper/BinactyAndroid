package com.mrboomdev.platformer.environment.pack;

import com.mrboomdev.platformer.environment.map.MapTile;
import java.util.List;
import java.util.Map;

public class PackMap {
	
	public static class Info {
		public String name = "Unknown author";
		public String description = "No description provided.";
		public String version = "1.0.0";
		public List<String> gamemodes;
	}
	
	public static class Maps {
		public Map<String, Info> special;
		public Map<String, Info> normal;
	}
	
	public static class Tiles {
		public Map<String, MapTile> tiles;
	}
}