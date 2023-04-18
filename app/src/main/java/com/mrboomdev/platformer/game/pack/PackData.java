package com.mrboomdev.platformer.game.pack;

import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Json;
import java.util.List;
import java.util.Map;

public class PackData {

	public static class Manifest {
		public String name, icon, description, id;
		public boolean required;
		public Author author;
		public Resources resources;
		@Json(ignore = true) public FileUtil source;
		@Json(ignore = true) public Config config;
		
		public boolean isValid() {
			return name != null;
		}
	}
	
	public static class Author {
		public String name, url, icon;
	}
	
	public static class Resources {
		public String gamemodes, characters;
	}
	
	public static class Tiles {
		public Map<String, MapTile> tiles;
	}
	
	public static class Config {
		public FileUtil file;
		public boolean active;
		
		public Config(FileUtil file) {
			this.file = file;
			this.active = true;
		}
	}
	
	public static class Gamemode {
		public String id, name, description, time, banner, type;
		public Author author;
		public int maxPlayers;
		public List<MapData> maps;
		public FileUtil source, file;
	}
	
	public static class GamemodesRow {
		public List<Gamemode> data;
		public String title, id;
	}
	
	public static class MapData {
		public String name;
		public FileUtil file;
		public Author author;
	}
}