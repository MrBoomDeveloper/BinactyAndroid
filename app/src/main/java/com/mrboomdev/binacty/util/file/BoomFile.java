package com.mrboomdev.binacty.util.file;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.Json;

import org.jetbrains.annotations.Contract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class BoomFile<T extends BoomFile<T>> {
	public final String path;

	@Override
	public boolean equals(@Nullable Object obj) {
		if(obj == null) return false;

		if(!(obj.getClass().isAssignableFrom(getClass()))) return false;

		var checkable = (T)obj;
		return getAbsolutePath().equals(checkable.getAbsolutePath());
	}

	public BoomFile(String path) {
		this.path = new File(path).getPath();
	}

	public String getUrl() {
		return "file://" + getAbsolutePath();
	}

	public String getAbsolutePath() {
		if(this instanceof ExternalBoomFile) {
			return BoomFile.global((ExternalBoomFile)this).getRelativePath();
		}

		return path;
	}

	public String getRelativePath() {
		return path;
	}

	public boolean exists() {
		return getFile().exists();
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

	public void copyTo(BoomFile<?> destination) {
		if(isDirectory()) {
			copyRecursivelyTo(destination);

			return;
		}

		try(var stream = getInputStream()) {
			destination.copyToMe(stream);
		} catch(IOException e) {
			throw new BoomException(e);
		}
	}

	public void copyToMe(InputStream input) {
		var parent = getParent();
		if(parent != this) parent.createDirectory();

		try(var stream = getOutputStream()) {
			copy(input, stream);
		} catch(IOException e) {
			throw new BoomException(e);
		}
	}

	public static void copy(@NonNull InputStream input, OutputStream out) {
		try {
			var buffer = new byte[1024];
			int read;

			while((read = input.read(buffer)) != -1) {
				out.write(buffer, 0 , read);
			}
		} catch(IOException e) {
			throw new BoomException(e);
		}
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

	public boolean isDirectory() {
		return getFile().isDirectory();
	}

	public List<T> list() {
		var list = new ArrayList<T>();
		var nativeList = getFile().listFiles();

		if(nativeList == null) return list;

		for(var child : nativeList) {
			list.add(goTo(child.getName()));
		}

		return list;
	}

	public List<T> listRecursively() {
		var list = new ArrayList<T>();

		for(var item : list()) {
			if(item.isDirectory()) {
				list.addAll(item.listRecursively());
			}

			list.add(item);
		}

		return list;
	}

	public List<T> listFilesRecursively() {
		var list = listRecursively();

		return list.stream()
				.filter(item -> !item.isDirectory())
				.collect(Collectors.toList());
	}

	public void copyRecursivelyTo(BoomFile<?> destination) {
		if(!isDirectory()) {
			copyTo(destination);
			return;
		}

		for(var item : list()) {
			var next = destination.goTo(item.getName());
			item.copyTo(next);
		}
	}

	public InputStream getInputStream() throws IOException {
		return new FileInputStream(getFile());
	}

	public OutputStream getOutputStream() throws IOException {
		return new FileOutputStream(getFile());
	}

	public void remove() {
		getFile().delete();
	}

	public String readString() {
		try(var stream = getInputStream()) {
			int size = stream.available();
			var buffer = new byte[size];

			stream.read(buffer);
			return new String(buffer);
		} catch(IOException e) {
			throw new BoomException(e);
		}
	}

	public void writeString(String string, boolean append) {
		try(var writer = new BufferedWriter(new FileWriter(getFile(), append))) {
			writer.write(string);
		} catch(IOException e) {
			throw new BoomException("Failed to write into a file", e);
		}
	}

	public byte[] readBytes() {
		try(var stream = getInputStream()) {
			int size = stream.available();
			var buffer = new byte[size];

			stream.read(buffer);
			return buffer;
		} catch(IOException e) {
			throw new BoomException("Failed to read bytes of a file!", e);
		}
	}

	public String getChecksum() {
		try {
			var md5 = MessageDigest.getInstance("MD5");
			var hash = md5.digest(readBytes());
			return new BigInteger(1, hash).toString(16);
		} catch(NoSuchAlgorithmException e) {
			throw new BoomException("MD5 isn't supported on you device!", e);
		}
	}

	public void writeString(String string) {
		writeString(string, false);
	}

	public void createDirectory() {
		getFile().mkdirs();
	}

	public File getFile() {
		return new File(getRelativePath());
	}

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
		if(getSource() == Source.EXTERNAL) return FileUtil.external(getRelativePath());
		if(getSource() == Source.INTERNAL) return FileUtil.internal(getRelativePath());
		if(getSource() == Source.GLOBAL) return new FileUtil(getRelativePath(), FileUtil.Source.FULL);

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