package com.mrboomdev.platformer.scenes.loading;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.mrboomdev.platformer.scenes.loading.LoadingFiles;
import com.mrboomdev.platformer.util.ColorUtil;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class LoadingFiles {
	public HashMap<String, Scene> scenes;
	
	public void loadToManager(AssetManager manager, String scene) {
		LoadingFiles.loadToManager(scenes.get(scene).load, manager);
	}
	
	public static void loadToManager(List<File> files, AssetManager manager) {
		for(File file : files) {
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
				FreeTypeFontLoaderParameter param = new FreeTypeFontLoaderParameter();
				param.fontFileName = file.path;
				param.fontParameters.size = file.font.size;
				param.fontParameters.color = file.font.color.getColor();
				param.fontParameters.borderWidth = file.font.borderSize;
				if(file.font.borderColor != null) {
					param.fontParameters.borderColor = file.font.borderColor.getColor();
				}
				manager.load(file.as, BitmapFont.class, param);
			}
		}
	}
	
	public class Scene {
		public ArrayList<File> load = new ArrayList<>();
		public ArrayList<String> unload = new ArrayList<>();
	}
	
	public static class File {
		public String path, as, type;
		public Font font;
		
		public File(String path, String type) {
			this.path = path;
			this.type = type;
		}
		
		public File(String path, String as, Font font) {
			this.path = path;
			this.as = as;
			this.type = "font";
			this.font = font;
		}
	}
	
	public class Font {
		public int size = 12, borderSize = 0;
		public ColorUtil color, borderColor;
	}
}