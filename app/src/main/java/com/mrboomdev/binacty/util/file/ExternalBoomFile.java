package com.mrboomdev.binacty.util.file;

import com.mrboomdev.platformer.ui.ActivityManager;

import java.io.File;

public class ExternalBoomFile extends BoomFile<ExternalBoomFile> {

	public ExternalBoomFile(String path) {
		super(path);
	}

	@Override
	public void remove() {
		removeRecursively(BoomFile.global(this));
	}

	@Override
	public File getFile() {
		var external = ActivityManager.current.getExternalFilesDir(null);
		return new File(external, getRelativePath());
	}
}