package com.mrboomdev.binacty.util.file;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.helper.BoomException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class InternalBoomFile extends BoomFile<InternalBoomFile> {

	public InternalBoomFile(String path) {
		super(path);
	}

	@Override
	public void copyTo(@NonNull BoomFile<?> destination) {
		try(var stream = ActivityManager.current.getAssets().open(getPath())) {
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
	public void remove() {
		throw new BoomException("Can't remove a file from a static environment!");
	}

	@Override
	public String readString() {
		try(var stream = ActivityManager.current.getAssets().open(getPath())) {
			int size = stream.available();
			var buffer = new byte[size];

			stream.read(buffer);
			return new String(buffer);
		} catch(IOException e) {
			throw new BoomException("Failed to read file content.", e);
		}
	}

	@Override
	public void createDirectory() {
		throw new BoomException("Can't create a directory inside of a static environment!");
	}

	@Override
	public File getFile() {
		throw new BoomException("Can't get a file from a static environment!");
	}
}