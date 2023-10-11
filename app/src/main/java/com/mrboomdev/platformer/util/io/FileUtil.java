package com.mrboomdev.platformer.util.io;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.jetbrains.annotations.Contract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FileUtil {
	public final String path;
	public final Source source;
	@Json(ignore = true)
	private static JsonAdapter<FileUtil> jsonAdapter;
	@Json(ignore = true)
	private static final String errorMessageBase = "Error while operating with FileUtil! ";
	@Json(ignore = true)
	private static final String errorMessageUnknownSource = errorMessageBase + "Unknown source or it is undefined. ";
	@Json(ignore = true)
	private static final String errorMessageIO = errorMessageBase + "We don't know the reason of the error. Only the type. It is IOException!";
	@Json(ignore = true)
	private static final String errorMessageNotFound = errorMessageBase + "File not found! ";
	@Json(ignore = true)
	private static final String errorMessageUnknown = errorMessageBase + "Something unexpected has happened! ";

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
	
	public String readString(boolean isGdxThread) {
		switch(source) {
			case INTERNAL: if(isGdxThread) {
				return getFileHandle().readString();
			} else {
				try(var stream = ActivityManager.current.getAssets().open(getPath())) {
					var buffer = new byte[stream.available()];
					stream.read(buffer);
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

	public void copy(FileUtil destination) throws BoomException {
		switch(source) {
			case INTERNAL: {
				try(var stream = ActivityManager.current.getAssets().open(getPath())) {
					destination.copyToMe(stream);
				} catch(IOException e) {
					throw new BoomException("Failed to copy a file from assets! " + getPath(), e);
				}
			}

			case FULL: {
				throw new BoomException("Currently unavailable!");
			}

			default: throw new BoomException("Unavailable destination!");
		}
	}

	private void copyToMe(InputStream inputStream) throws BoomException {
		switch(source) {
			case EXTERNAL: {
				var path = new File(ActivityManager.current.getExternalFilesDir(null), getPath());

				try(var outStream = new FileOutputStream(path)) {
					copy(inputStream, outStream);
				} catch(IOException e) {
					throw new BoomException(e);
				}
			}

			case FULL: {
				try(var outStream = new FileOutputStream(getPath())) {
					copy(inputStream, outStream);
				} catch(IOException e) {
					throw new BoomException(e);
				}
			}

			default: throw new BoomException("Unavailable destination!");
		}
	}

	private static void copy(@NonNull InputStream input, OutputStream out) throws IOException {
		var buffer = new byte[1024];
		int read;

		while((read = input.read(buffer)) != -1) {
			out.write(buffer, 0 , read);
		}
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

	public boolean isLoadedAsync() {
		var game = GameHolder.getInstance();
		var assetsManager = source == Source.EXTERNAL ? game.externalAssets : game.assets;

		return assetsManager.isLoaded(getPath());
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
		switch(source) {
			case INTERNAL: throw BoomException.builder("Failed to remove a file. Can't edit internal assets! Path: ").addQuoted(getPath()).build();
			case FULL:
			case EXTERNAL: {
				var file = new File(getFullPath(false));

				if(file.isDirectory()) {
					var list = file.listFiles();
					if(list == null) return;

					for(var child : list) {
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
			case INTERNAL: throw BoomException.builder("Failed to rename a file. Can't edit internal assets! Path: ").addQuoted(getPath()).build();
			case EXTERNAL: {
				var file = getFile();
				file.renameTo(new File(file.getParent() + "/" + name));
				break;
			}
		}
	}
	
	public File getFile() {
		if(source != Source.EXTERNAL) return new File(getPath());
		return new File(ActivityManager.current.getExternalFilesDir(null), path);
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

	public enum Source {
		INTERNAL,
		EXTERNAL,
		FULL
	}
}