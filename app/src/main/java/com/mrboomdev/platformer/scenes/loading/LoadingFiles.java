package com.mrboomdev.platformer.scenes.loading;

import androidx.annotation.NonNull;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.mrboomdev.platformer.util.ColorUtil;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LoadingFiles {
	public Map<String, Scene> scenes;
	
	public void loadToManager(AssetManager manager, String scene) {
		LoadingFiles.loadToManager(Objects.requireNonNull(scenes.get(scene)).load, "", manager);
	}
	
	public static void loadToManager(@NonNull List<File> files, String source, AssetManager manager) {
		for(File file : files) {
			String path = source + file.path;
			
			if(file.type.equals("texture")) {
				manager.load(path, Texture.class);
			}
			if(file.type.equals("music")) {
				manager.load(path, Music.class);
			}
			if(file.type.equals("sound")) {
				manager.load(path, Sound.class);
			}
			if(file.type.equals("font")) {
				FreeTypeFontLoaderParameter param = new FreeTypeFontLoaderParameter();
				param.fontFileName = path;
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
	
	public static class Scene {
		public List<File> load;
	}
	
	public static class File {
		public String path, as, type;
		public Font font;
	}
	
	public static class Font {
		public int size = 12, borderSize = 0;
		public ColorUtil color, borderColor;
	}
}