package com.mrboomdev.binacty.script.bridge;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mrboomdev.binacty.api.resources.BinactyFile;
import com.mrboomdev.binacty.api.resources.BinactyResources;
import com.mrboomdev.binacty.api.resources.BinactyTexture;
import com.mrboomdev.binacty.api.resources.audio.Music;
import com.mrboomdev.binacty.api.resources.audio.Sound;
import com.mrboomdev.binacty.script.bridge.resources.MyFile;
import com.mrboomdev.binacty.script.bridge.resources.MyMusic;
import com.mrboomdev.binacty.script.bridge.resources.MySound;
import com.mrboomdev.binacty.script.bridge.resources.MyTexture;
import com.mrboomdev.binacty.util.file.BoomFile;

public class MyResources extends BinactyResources {
	private final BoomFile<?> resourcesRoot;

	public MyResources(@NonNull MyPackContext context) {
		var entry = context.entry;

		this.resourcesRoot = BoomFile.fromString(
				entry.resourcesPath,
				entry.resourcesSource);
	}

	@Override
	public BinactyTexture getTexture(String path) {
		var file = resourcesRoot.goTo(path).toFileUtil().getFileHandle();
		return new MyTexture(new Texture(file));
	}

	@Override
	public Music getMusic(String path) {
		var file = resourcesRoot.goTo(path).toFileUtil().getFileHandle();
		return new MyMusic(Gdx.audio.newMusic(file));
	}

	@Override
	public Sound getSound(String path) {
		var file = resourcesRoot.goTo(path).toFileUtil().getFileHandle();
		return new MySound(Gdx.audio.newSound(file));
	}

	@Override
	public BinactyFile getFile(String path) {
		return new MyFile(path);
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
	public void unload(String path) {

	}

	@Override
	public boolean isLoaded() {
		return false;
	}
}