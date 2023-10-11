package com.mrboomdev.binacty.util.file;

import com.mrboomdev.platformer.util.helper.BoomException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
		getParent().createDirectory();

		try(var out = new FileOutputStream(getFile())) {
			copy(input, out);
		} catch(IOException e) {
			throw new BoomException(e);
		}
	}

	@Override
	public boolean isDirectory() {
		return getFile().isDirectory();
	}

	@Override
	public List<GlobalBoomFile> list() {
		throw new BoomException("Stub!");
	}

	@Override
	public void remove() {
		removeRecursively(this);
	}

	@Override
	public String readString() {
		try(var stream = new FileInputStream(getFile())) {
			return readString(stream);
		} catch(IOException e) {
			throw new BoomException("Failed to read file content.", e);
		}
	}

	@Override
	public void writeString(String string, boolean append) {
		throw new BoomException("Stub!");
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