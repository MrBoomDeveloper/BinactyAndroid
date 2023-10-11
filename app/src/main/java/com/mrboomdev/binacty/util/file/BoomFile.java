package com.mrboomdev.binacty.util.file;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.helper.BoomException;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unchecked")
public abstract class BoomFile<T extends BoomFile<T>> {
	public final String path;

	@Override
	public boolean equals(@Nullable Object obj) {
		if(obj == null) return false;

		if(!(obj.getClass().isAssignableFrom(getClass()))) return false;

		var checkable = (T)obj;
		return path.equals(checkable.path);
	}

	public BoomFile(String path) {
		this.path = new File(path).getPath();
	}

	public String getPath() {
		return path;
	}

	public T goTo(String path) {
		var newPath = new File(getPath() + "/" + path).getPath();

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
	public static GlobalBoomFile global(@NonNull BoomFile<?> file) {
		if(file instanceof GlobalBoomFile) {
			return (GlobalBoomFile) file;
		}

		if(file instanceof InternalBoomFile) {
			throw new BoomException("Cannot get a global instance of an internal file!");
		}

		var external = ActivityManager.current.getExternalFilesDir(null);
		var path = new File(external, file.getPath());

		return new GlobalBoomFile(path.getPath());
	}
}