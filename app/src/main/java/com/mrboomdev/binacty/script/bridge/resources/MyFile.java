package com.mrboomdev.binacty.script.bridge.resources;

import com.mrboomdev.binacty.api.resources.BinactyFile;
import com.mrboomdev.binacty.util.file.BoomFile;

public class MyFile extends BinactyFile {
	private BoomFile<?> nativeFile;

	public MyFile(String path) {
		super(path);
	}

	@Override
	public String readString() {
		return nativeFile.readString();
	}

	@Override
	public BinactyFile goTo(String s) {
		return null;
	}
}