package com.mrboomdev.platformer.environment.pack;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PackGamemodes {
	public String status = "loading";
	public ArrayList<Gamemode> special = new ArrayList<>();
	public ArrayList<Gamemode> other = new ArrayList<>();
	
	private class Gamemode {
		public ArrayList<String> maps = new ArrayList<>();
		public String name = "New Gamemode";
		public String description = "No description.";
		public String banner = "";
		public String patn = "";
		public PackManifest.Author author;
	}
	
	public static PackGamemodes scan(Context context) {
		PackGamemodes modes = new PackGamemodes();
		
		AssetManager assets = context.getAssets();
		try {
			InputStream stream = assets.open("world/packs/core/gamemodes.json");
			stream.close();
			modes.status = "successful";
		} catch(IOException e) {
			modes.status = "error";
			e.printStackTrace();
		}
		
		return null;
	}
}