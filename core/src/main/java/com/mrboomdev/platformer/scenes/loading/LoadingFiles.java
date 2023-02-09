package com.mrboomdev.platformer.scenes.loading;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.mrboomdev.platformer.util.ColorUtil;
import java.util.HashMap;
import java.util.ArrayList;

public class LoadingFiles {
	public HashMap<String, Scene> scenes;
	
	public class Scene {
		public ArrayList<File> load = new ArrayList<>();
		public ArrayList<String> unload = new ArrayList<>();
	}
	
	public class File {
		public String path, type;
		public Font font;
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
				FreeTypeFontLoaderParameter param = new FreeTypeFontLoaderParameter();
				param.fontFileName = file.path;
				param.fontParameters.size = file.font.size;
				param.fontParameters.color = file.font.color.getColor();
				param.fontParameters.borderWidth = file.font.borderSize;
				if(file.font.borderColor != null) {
					param.fontParameters.borderColor = file.font.borderColor.getColor();
				}
				manager.load(file.path, BitmapFont.class, param);
			}
		}
	}
	
	public class Font {
		public int size = 12, borderSize = 0;
		public ColorUtil color, borderColor;
	}
}