package com.mrboomdev.platformer.environment.pack;

import com.mrboomdev.platformer.util.io.FileUtil;

public class Pack {
	public String name = "Untitled";
	public String description = "No description provided.";
	public String icon, version = "1.0.0";
	public boolean required, active;
	public String path;
	public FileUtil.Source source;
	public Author author;
	public Resources resources;
	
	public Pack() {} //Approach for moshi to keep default fields
	
	public static class Author {
		public String name = "Unknown author";
		public String icon, url;
	}
	
	public static class Resources {
		public String gamemodes;
		public String maps;
		public String characters;
	}
}