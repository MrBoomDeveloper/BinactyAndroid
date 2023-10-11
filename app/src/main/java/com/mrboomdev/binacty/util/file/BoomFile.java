package com.mrboomdev.binacty.util.file;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Json;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class BoomFile<T extends BoomFile<T>> {
	public final String path;

	@Override
	public boolean equals(@Nullable Object obj) {
		if(obj == null) return false;

		if(!(obj.getClass().isAssignableFrom(getClass()))) return false;

		var checkable = (T)obj;
		return getPath().equals(checkable.getPath());
	}

	public BoomFile(String path) {
		this.path = new File(path).getPath();
	}

	public String getPath() {
		return path;
	}

	public String getRelativePath() {
		return path;
	}

	public T goTo(String path) {
		var newPath = new File(getRelativePath() + "/" + path).getPath();

		try {
			var constructor = getClass().getConstructor(String.class);
			return (T)constructor.newInstance(newPath);
		} catch(InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
			throw new BoomException("Failed to create a new BoomFile!", e);
		}
	}

	public abstract void copyTo(BoomFile<?> destination);

	public abstract void copyToMe(InputStream input);

	public static void copy(@NonNull InputStream input, OutputStream out) throws IOException {
		var buffer = new byte[1024];
		int read;

		while((read = input.read(buffer)) != -1) {
			out.write(buffer, 0 , read);
		}
	}

	@NonNull
	@Contract("_ -> new")
	public static String readString(@NonNull InputStream stream) throws IOException {
		int size = stream.available();
		var buffer = new byte[size];

		stream.read(buffer);
		return new String(buffer);
	}

	public static void removeRecursively(@NonNull GlobalBoomFile boomFile) {
		var file = boomFile.getFile();

		if(file.isDirectory()) {
			for(var child : boomFile.listRecursively()) {
				BoomFile.global(child).remove();
			}
		}

		file.delete();
	}

	public abstract boolean isDirectory();

	public abstract List<T> list();

	public abstract List<T> listRecursively();

	public abstract void remove();

	public abstract String readString();

	public abstract void createDirectory();

	public abstract File getFile();

	@NonNull
	@Contract("_ -> new")
	public static InternalBoomFile internal(String path) {
		return new InternalBoomFile(path);
	}

	@NonNull
	@Contract("_ -> new")
	public static ExternalBoomFile external(String path) {
		return new ExternalBoomFile(path);
	}

	@NonNull
	@Contract("_ -> new")
	public static GlobalBoomFile global(String path) {
		return new GlobalBoomFile(path);
	}

	@NonNull
	public static GlobalBoomFile global(@NonNull BoomFile<?> file) {
		if(file instanceof GlobalBoomFile) {
			return (GlobalBoomFile) file;
		}

		if(file instanceof InternalBoomFile) {
			throw new BoomException("Cannot get a global instance of an internal file!");
		}

		var external = ActivityManager.current.getExternalFilesDir(null);
		var path = new File(external, file.getRelativePath());

		return new GlobalBoomFile(path.getPath());
	}

	@NonNull
	@Override
	public String toString() {
		return "{ \"source\": \"" + getSource().name().toLowerCase() + "\", " +
				 "\"path\": \""   + getRelativePath() + "\" }";
	}

	public String getName() {
		return getFile().getName();
	}

	public T getParent() {
		try {
			var parentPath = new File(getRelativePath()).getParent();
			return (T)BoomFile.fromString(parentPath, getSource());
		} catch(Exception e) {
			e.printStackTrace();
			return (T)this;
		}
	}

	@NonNull
	public static BoomFile<?> fromString(String path, Source source) {
		if(source == Source.EXTERNAL) return external(path);
		if(source == Source.INTERNAL) return internal(path);
		if(source == Source.GLOBAL) return global(path);

		throw new BoomException("Unknown source of file!");
	}

	public Source getSource() {
		if(this instanceof ExternalBoomFile) return Source.EXTERNAL;
		if(this instanceof InternalBoomFile) return Source.INTERNAL;
		if(this instanceof GlobalBoomFile) return Source.GLOBAL;

		throw new BoomException("Unknown source!");
	}

	public FileUtil toFileUtil() {
		if(getSource() == Source.EXTERNAL) return FileUtil.external(path);
		if(getSource() == Source.INTERNAL) return FileUtil.internal(path);
		if(getSource() == Source.GLOBAL) return new FileUtil(path, FileUtil.Source.FULL);

		throw new BoomException("Unknown source of file!");
	}

	public enum Source {
		@Json(name = "internal")
		INTERNAL,
		@Json(name = "external")
		EXTERNAL,
		@Json(name = "global")
		GLOBAL
	}
}