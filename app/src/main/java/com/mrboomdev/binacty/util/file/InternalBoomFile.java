package com.mrboomdev.binacty.util.file;

import android.content.res.AssetManager;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.helper.BoomException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InternalBoomFile extends BoomFile<InternalBoomFile> {

	public InternalBoomFile(String path) {
		super(path);
	}

	@Override
	public void copyTo(@NonNull BoomFile<?> destination) {
		if(isDirectory()) {
			for(var item : list()) {
				var next = destination.goTo(item.getName());
				item.copyTo(next);
			}

			return;
		}

		try(var stream = getAssets().open(getRelativePath())) {
			destination.copyToMe(stream);
		} catch(IOException e) {
			throw new BoomException(e);
		}
	}

	@Override
	public void copyToMe(InputStream input) {
		throw new BoomException("Can't copy a file into a static environment!");
	}

	@Override
	public boolean isDirectory() {
		try {
			getAssets().open(getRelativePath()).close();
			return false;
		} catch(IOException e) {
			return true;
		}
	}

	@Override
	public List<InternalBoomFile> list() {
		var list = new ArrayList<InternalBoomFile>();
		String[] nativeList;

		try {
			nativeList = Objects.requireNonNull(getAssets().list(getRelativePath()));
		} catch(IOException | NullPointerException e) {
			e.printStackTrace();
			return list;
		}

		for(var child : nativeList) {
			list.add(goTo(new File(child).getName()));
		}

		return list;
	}

	@Override
	public void remove() {
		throw new BoomException("Can't remove a file from a static environment!");
	}

	@Override
	public String readString() {
		try(var stream = getAssets().open(getRelativePath())) {
			return readString(stream);
		} catch(IOException e) {
			throw new BoomException("Failed to read file content.", e);
		}
	}

	@Override
	public void writeString(String string, boolean append) {
		throw new BoomException("Can't write into a file from a static environment!");
	}

	@Override
	public void createDirectory() {
		throw new BoomException("Can't create a directory inside of a static environment!");
	}

	@Override
	public File getFile() {
		return new File(getRelativePath());
	}

	private AssetManager getAssets() {
		return ActivityManager.current.getAssets();
	}
}