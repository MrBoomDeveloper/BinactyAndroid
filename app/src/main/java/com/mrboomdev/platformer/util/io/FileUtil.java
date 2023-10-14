package com.mrboomdev.platformer.util.io;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
	public final String path;
	public final Source source;
	@Json(ignore = true)
	private static JsonAdapter<FileUtil> jsonAdapter;

	public FileUtil(String path, Source source) {
		this.path = path;
		this.source = source;
	}

	public static FileUtil fromJson(String json) {
		generateJsonAdapter();

		try {
			return jsonAdapter.fromJson(json);
		} catch(IOException e) {
			throw new BoomException("Failed to parse a json", e);
		}
	}

	@NonNull
	@Contract(value = "_ -> new", pure = true)
	public static FileUtil external(String path) {
		return new FileUtil(path, Source.EXTERNAL);
	}
	
	@NonNull
	@Contract(value = "_ -> new", pure = true)
	public static FileUtil internal(String path) {
		return new FileUtil(path, Source.INTERNAL);
	}
	
	public String readString() {
		return toBoomFile().readString();
	}

	public void copy(@NonNull FileUtil destination) throws BoomException {
		toBoomFile().copyTo(destination.toBoomFile());
	}

	public void writeString(String text, boolean isGdxThread) {
		if(source != Source.EXTERNAL) return;

		if(isGdxThread) {
			Gdx.files.external(path).writeString(text, false);
			return;
		}

		File file = new File(ActivityManager.current.getExternalFilesDir(null), path);
		File parent = file.getParentFile();
		if(parent != null && !parent.exists() && !parent.mkdirs()) return;

		try(FileOutputStream stream = new FileOutputStream(file)) {
			stream.write(text.getBytes());
		} catch(Exception e) {
			throw new BoomException(e);
		}
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
			result = result.substring(1);
		}

		return result;
	}
	
	public void loadAsync(Class<?> clazz) {
		var game = GameHolder.getInstance();

		if(source == Source.EXTERNAL) {
			game.externalAssets.load(getPath(), clazz);
		} else {
			game.assets.load(getPath(), clazz);
		}
	}
	
	public boolean isAddedToAsyncLoading() {
		var game = GameHolder.getInstance();

		switch(source) {
			case INTERNAL: return game.assets.contains(getPath());
			case EXTERNAL: return game.externalAssets.contains(getPath());
			default: return false;
		}
	}
	
	public <T> T getLoaded(Class<T> clazz) {
		var game = GameHolder.getInstance();
		switch(source) {
			case INTERNAL: return game.assets.get(getPath(), clazz);
			case EXTERNAL: return game.externalAssets.get(getPath(), clazz);
			default: return null;
		}
	}
	
	public String getFullPath(boolean isUrl) {
		switch(source) {
			case EXTERNAL: {
				var file = new File(ActivityManager.current.getExternalFilesDir(null), path);
				return isUrl ? ("file://" + file.getAbsolutePath()) : file.getAbsolutePath();
			}

			case INTERNAL: return isUrl ? ("asset:/" + getPath()) : getPath();
			default: return isUrl ? "file:///" + getPath() : getPath();
		}
	}
	
	public void remove() {
		toBoomFile().remove();
	}
	
	public void rename(String name) {
		switch(source) {
			case INTERNAL: throw BoomException.builder("Failed to rename a file. Can't edit internal assets! Path: ").addQuoted(getPath()).build();
			case EXTERNAL: {
				var file = getFile();
				file.renameTo(new File(file.getParent() + "/" + name));
				break;
			}
		}
	}
	
	public File getFile() {
		return toBoomFile().getFile();
	}
	
	public FileHandle getFileHandle() {
		if(source == Source.EXTERNAL) return Gdx.files.external(getPath());
		return Gdx.files.internal(getPath());
	}
	
	@Override
	public boolean equals(Object object) {
		if(!(object instanceof FileUtil)) return false;
		var file = (FileUtil)object;
		if(!file.getPath().equals(getPath())) return false;
		return file.source == source;
	}

	@NonNull
	@Override
	public String toString() {
		generateJsonAdapter();
		return jsonAdapter.toJson(this);
	}

	private static void generateJsonAdapter() {
		if(jsonAdapter == null) {
			var moshi = new Moshi.Builder().build();
			jsonAdapter = moshi.adapter(FileUtil.class);
		}
	}

	public BoomFile<?> toBoomFile() {
		if(source == Source.INTERNAL) return BoomFile.internal(getPath());
		if(source == Source.EXTERNAL) return BoomFile.external(getPath());
		if(source == Source.FULL) return BoomFile.global(getPath());

		throw new BoomException("Unknown source");
	}

	public enum Source { INTERNAL, EXTERNAL, FULL }
}