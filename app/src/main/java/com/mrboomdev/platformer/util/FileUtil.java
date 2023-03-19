package com.mrboomdev.platformer.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mrboomdev.platformer.game.GameHolder;
import java.io.File;
import java.io.IOException;

public class FileUtil {
	public String path;
	public Source source;
	private boolean loadAsync = false;
	
	public FileUtil(String path, Source source, boolean loadAsync) {
		this.path = path;
		this.source = source;
		this.loadAsync = loadAsync;
	}
	
	public FileUtil(String path, Source source) {
		this(path, source, false);
	}
	
	public String readString(boolean isGdxThread) {
		return getHandle().readString();
	}
	
	public String getName() {
		return new File(path).getName();
	}
	
	public FileUtil goTo(String path) {
		try {
			return new FileUtil(new File(this.path, path).getCanonicalPath(), source);
		} catch(IOException e) {
			e.printStackTrace();
			return this;
		}
	}
	
	public FileUtil getParent() {
		return new FileUtil(new File(path).getParent() + "/", source);
	}
	
	public String getPath() {
		String result = path;
		if(result.startsWith("/")) {
			result = result.substring(1, result.length());
		}
		return result;
	}
	
	public <T> void load(Class<T> className) {
		if(!loadAsync) return;
		
		var game = GameHolder.getInstance();
		if(source == Source.INTERNAL) game.assets.load(getPath(), className);
		if(source == Source.EXTERNAL) game.externalAssets.load(getPath(), className);
		if(source == Source.NETWORK) game.externalAssets.load("cache/" + getPath(), className);
	}
	
	public FileHandle getHandle() {
		if(!loadAsync) return Gdx.files.internal(getPath());
		
		var game = GameHolder.getInstance();
		if(source == Source.EXTERNAL) return game.externalAssets.get(getPath());
		if(source == Source.NETWORK) return game.externalAssets.get("cache/" + getPath());
		
		return game.assets.get(getPath());
	}
	
	public enum Source {
		INTERNAL,
		EXTERNAL,
		NETWORK
	}
}