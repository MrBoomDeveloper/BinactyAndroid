package com.mrboomdev.platformer.util;

import com.badlogic.gdx.Gdx;
import java.io.File;

public class FileUtil {
	public String path, directory;
	public Source source;
	
	public FileUtil(String path, Source source) {
		this.path = path;
		this.source = source;
		this.directory = path.substring(0, path.lastIndexOf("/"));
	}
	
	public String readString() {
		switch(source) {
			case INTERNAL:
				return Gdx.files.internal(path).readString();
			default:
				return "";
		}
	}
	
	public String getParentPath() {
		return new File(directory).getParent() + "/";
	}
	
	public String concatPath(String path) {
		if(path.substring(0, 3).equals("../")) {
			return getParentPath() + path.substring(3, path.length());
		}
		return this.directory + path;
	}
	
	public enum Source {
		INTERNAL,
		EXTERNAL,
		NETWORK
	}
}