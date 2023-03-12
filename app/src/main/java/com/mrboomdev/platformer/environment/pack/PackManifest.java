package com.mrboomdev.platformer.environment.pack;

import java.util.List;

public class PackManifest {
	public static final String coreManifestPath = "";
	public String name = "New Pack";
	public String description = "No description.";
	public String icon = "";
	public boolean required = false;
	public Author author = new Author();
	public Resources resources;
	
	public class Author {
		public String name = "Unknown author";
		public String icon = "";
		public String url = "";
	}
	
	public class Resources {
		public String maps;
		public String gamemodes;
		public String characters;
	}
	
	public static List<PackManifest> scan() {
		return null;
	}
}