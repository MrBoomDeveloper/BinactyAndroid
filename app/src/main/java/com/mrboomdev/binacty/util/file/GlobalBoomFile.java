package com.mrboomdev.binacty.util.file;

public class GlobalBoomFile extends BoomFile<GlobalBoomFile> {

	public GlobalBoomFile(String path) {
		super(path);
	}

	@Override
	public void remove() {
		removeRecursively(this);
	}
}