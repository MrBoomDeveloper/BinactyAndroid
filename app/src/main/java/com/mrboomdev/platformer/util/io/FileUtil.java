package com.mrboomdev.platformer.util.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.squareup.moshi.Json;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileUtil {
	public String path;
	public Source source;
	@Json(ignore = true) static final String errorMessageBase = "Error while operating with FileUtil! ";
	@Json(ignore = true) static final String errorMessageUnknownSource = errorMessageBase + "Unknown source or it is undefined. ";
	@Json(ignore = true) static final String errorMessageIO = errorMessageBase + "We don't know the reason of the error. Only the type. It is IOException!";
	@Json(ignore = true) static final String errorMessageNotFound = errorMessageBase + "File not found! ";
	@Json(ignore = true) static final String errorMessageUnknown = errorMessageBase + "Something unexpected has happened! ";
	@Json(ignore = true) boolean loadAsync = false;
	
	public FileUtil(String path, Source source, boolean loadAsync) {
		this.path = path;
		this.source = source;
		this.loadAsync = loadAsync;
	}
	
	public FileUtil(String path, Source source) {
		this(path, source, false);
	}
	
	public static FileUtil external(String path) {
		return new FileUtil(path, Source.EXTERNAL);
	}
	
	public static FileUtil internal(String path) {
		return new FileUtil(path, Source.INTERNAL);
	}
	
	public String readString(boolean isGdxThread) {
		switch(source) {
			case INTERNAL: if(isGdxThread) {
				return getHandle().readString();
			} else {
				try {
					var stream = ActivityManager.current.getAssets().open(getPath());
          	      var buffer = new byte[stream.available()];
              	  stream.read(buffer);
               	 stream.close();
					return new String(buffer);
				} catch(IOException e) {
					e.printStackTrace();
					return errorMessageIO + e.getMessage();
				} catch(Exception e) {
					e.printStackTrace();
					return errorMessageUnknown + e.getMessage();
				}
			}
			case EXTERNAL: {
				try {
					FileInputStream fis = new FileInputStream(new File(ActivityManager.current.getExternalFilesDir(null), path));
					InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
					StringBuilder stringBuilder = new StringBuilder();
					try(BufferedReader reader = new BufferedReader(inputStreamReader)) {
						String line = reader.readLine();
						while(line != null) {
							stringBuilder.append(line).append('\n');
							line = reader.readLine();
						}
					}
					return stringBuilder.toString();
				} catch(FileNotFoundException e) {
					e.printStackTrace();
					return errorMessageNotFound + e.getMessage();
				} catch (IOException e) {
					e.printStackTrace();
					return errorMessageIO + e.getMessage();
				} catch(Exception e) {
					e.printStackTrace();
					return errorMessageUnknown + e.getMessage();
				}
			}
			default: return errorMessageUnknownSource;
		}
	}
	
	public boolean writeString(String text, boolean isGdxThread) {
		switch(source) {
			case EXTERNAL: if(isGdxThread) {
				Gdx.files.external(path).writeString(text, false);
				return true;
			} else {
				try {
					File file = new File(ActivityManager.current.getExternalFilesDir(null), path);
					if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
               	 Files.write(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
					return true;
				} catch(IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			case INTERNAL: return false;
		}
		return false;
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
	
	public void loadAsync(Class<?> clazz) {
		var game = GameHolder.getInstance();
		switch(source) {
			case INTERNAL: {
				game.assets.load(getPath(), clazz);
				break;
			}
			case EXTERNAL: {
				game.externalAssets.load(getPath(), clazz);
				break;
			}
		}
	}
	
	public <T> T getLoaded(Class<T> clazz) {
		var game = GameHolder.getInstance();
		switch(source) {
			case INTERNAL: {
				return game.assets.get(getPath(), clazz);
			}
			case EXTERNAL: {
				return game.externalAssets.get(getPath(), clazz);
			}
			default: return null;
		}
	}
	
	public String getFullPath(boolean isUrl) {
		switch(source) {
			case EXTERNAL: {
				var file = new File(ActivityManager.current.getExternalFilesDir(null), path);
				return isUrl ? ("file://" + file.getAbsolutePath()) : file.getAbsolutePath();
			}
			case FULL: {
				return getPath();
			}
			case INTERNAL: {
				return isUrl ? ("asset:/" + getPath()) : getPath();
			}
			default: return getPath();
		}
	}
	
	public void remove() {
		switch(source) {
			case INTERNAL: throw new RuntimeException("Can't remove internal assets!");
			case FULL:
			case EXTERNAL: {
				var file = new File(getFullPath(false));
				if(file.isDirectory()) {
					for(var child : file.listFiles()) {
						new FileUtil(child.getAbsolutePath(), Source.FULL).remove();
					}
				}
				file.delete();
				break;
			}
		}
	}
	
	public void rename(String name) {
		switch(source) {
			case INTERNAL: throw new RuntimeException("Can't rename file from the assets!");
			case EXTERNAL: {
				var file = getFile();
				file.renameTo(new File(file.getParent() + "/" + name));
				break;
			}
		}
	}
	
	public File getFile() {
		switch(source) {
			case EXTERNAL: return new File(ActivityManager.current.getExternalFilesDir(null), path);
			default: return new File(getPath());
		}
	}
	
	public <T> void load(Class<T> className) {
		if(!loadAsync) return;
		
		var game = GameHolder.getInstance();
		if(source == Source.INTERNAL) game.assets.load(getPath(), className);
		if(source == Source.EXTERNAL) game.externalAssets.load(getPath(), className);
		if(source == Source.NETWORK) game.externalAssets.load("cache/" + getPath(), className);
	}
	
	public FileHandle getFileHandle() {
		switch(source) {
			case EXTERNAL: return Gdx.files.external(getPath());
			default: return Gdx.files.internal(getPath());
		}
	}
	
	@Deprecated
	public FileHandle getHandle() {
		var game = GameHolder.getInstance();
		if(!loadAsync) return Gdx.files.internal(getPath());
		if(source == Source.EXTERNAL) return game.externalAssets.get(getPath());
		if(source == Source.NETWORK) return game.externalAssets.get("cache/" + getPath());
		return game.assets.get(getPath());
	}
	
	@Override
	public boolean equals(Object object) {
		if(!(object instanceof FileUtil)) return false;
		var file = (FileUtil)object;
		if(!file.getPath().equals(getPath())) return false;
		if(file.source != source) return false;
		return true;
	}
	
	public enum Source {
		INTERNAL,
		EXTERNAL,
		FULL,
		NETWORK
	}
}