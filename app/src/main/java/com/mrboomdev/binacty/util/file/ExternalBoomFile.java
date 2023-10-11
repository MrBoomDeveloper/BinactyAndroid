package com.mrboomdev.binacty.util.file;

import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.helper.BoomException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExternalBoomFile extends BoomFile<ExternalBoomFile> {

	public ExternalBoomFile(String path) {
		super(path);
	}

	@Override
	public void copyTo(BoomFile<?> destination) {
		throw new BoomException("Stub!");
	}

	@Override
	public void copyToMe(InputStream input) {
		var parent = getParent();
		if(parent != this) parent.createDirectory();

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
	public List<ExternalBoomFile> list() {
		var list = new ArrayList<ExternalBoomFile>();
		var nativeList = getFile().listFiles();
		if(nativeList == null) return list;

		for(var child : nativeList) {
			list.add(goTo(child.getName()));
		}

		return list;
	}

	@Override
	public List<ExternalBoomFile> listRecursively() {
		return null;
	}

	@Override
	public void remove() {
		removeRecursively(BoomFile.global(this));
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
	public void createDirectory() {
		getFile().mkdirs();
	}

	@Override
	public File getFile() {
		var external = ActivityManager.current.getExternalFilesDir(null);
		return new File(external, getPath());
	}
}