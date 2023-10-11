package com.mrboomdev.binacty.util.file;

import com.mrboomdev.platformer.util.helper.BoomException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GlobalBoomFile extends BoomFile<GlobalBoomFile> {

	public GlobalBoomFile(String path) {
		super(path);
	}

	@Override
	public void copyTo(BoomFile<?> destination) {
		throw new BoomException("Stub!");
	}

	@Override
	public void copyToMe(InputStream input) {
		throw new BoomException("Stub!");
	}

	@Override
	public void remove() {
		getFile().delete();
	}

	@Override
	public String readString() {
		try(var stream = new FileInputStream(getFile())) {
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
		getFile().mkdirs();
	}

	@Override
	public File getFile() {
		return new File(getPath());
	}
}