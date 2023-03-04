package com.mrboomdev.platformer.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;
import java.io.IOException;

public class FileUtil {
	public String path;
	public Source source;
	
	public FileUtil(String path, Source source) {
		this.path = path;
		this.source = source;
	}
	
	public String readString() {
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
	
	public FileHandle getHandle() {
		return Gdx.files.internal(getPath());
	}
	
	public enum Source {
		INTERNAL,
		EXTERNAL,
		NETWORK
	}
}