package com.mrboomdev.platformer.scenes.loading;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.ArrayList;

public class LoadingFiles {
	public HashMap<String, Scene> scenes;
	
	public class Scene {
		public ArrayList<File> load = new ArrayList<>();
		public ArrayList<String> unload = new ArrayList<>();
	}
	
	public class File {
		public String path, as, type;
		public int size = 12;
	}
	
	public void loadToManager(AssetManager manager, String scene) {
		for(File file : scenes.get(scene).load) {
			if(file.type.equals("texture")) {
				manager.load(file.path, Texture.class);
			}
			if(file.type.equals("music")) {
				manager.load(file.path, Music.class);
			}
			if(file.type.equals("sound")) {
				manager.load(file.path, Sound.class);
			}
			if(file.type.equals("font")) {
				continue;
			}
		}
	}
}