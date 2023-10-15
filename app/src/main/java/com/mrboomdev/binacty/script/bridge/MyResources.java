package com.mrboomdev.binacty.script.bridge;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.mrboomdev.binacty.api.resources.BinactyFile;
import com.mrboomdev.binacty.api.resources.BinactyResources;
import com.mrboomdev.binacty.api.resources.audio.Music;
import com.mrboomdev.binacty.api.resources.audio.Sound;
import com.mrboomdev.binacty.script.bridge.resources.MyMusic;
import com.mrboomdev.binacty.util.file.BoomFile;
import com.mrboomdev.platformer.util.helper.BoomException;

public class MyResources extends BinactyResources {
	private final BoomFile<?> resourcesRoot;

	public MyResources(@NonNull MyPackContext context) {
		var entry = context.entry;

		this.resourcesRoot = BoomFile.fromString(entry.resourcesPath, entry.resourcesSource);
	}

	@Override
	public Music getMusic(String path) {
		var file = resourcesRoot.goTo(path).toFileUtil().getFileHandle();
		return new MyMusic(Gdx.audio.newMusic(file));
	}

	@Override
	public Sound getSound(String path) {
		return null;
	}

	@Override
	public BinactyFile getFile(String path) {
		return null;
	}

	@Override
	public boolean isLoaded(String path) {
		return false;
	}

	@Override
	public void loadMusic(String path) {

	}

	@Override
	public void loadSound(String path) {

	}

	@Override
	public void loadTexture(String path) {

	}

	@Override
	public void setBackend(BinactyResources binactyResources) {
		throw new BoomException("REMOVE ME FROM THE API!!!");
	}

	@Override
	public void unload(String path) {

	}

	@Override
	public boolean isLoaded() {
		return false;
	}
}