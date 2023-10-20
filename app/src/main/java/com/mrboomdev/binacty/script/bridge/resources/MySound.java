package com.mrboomdev.binacty.script.bridge.resources;

import com.mrboomdev.binacty.api.resources.audio.Sound;

public class MySound extends Sound {
	private final com.badlogic.gdx.audio.Sound nativeSound;

	public MySound(com.badlogic.gdx.audio.Sound nativeSound) {
		this.nativeSound = nativeSound;
	}

	@Override
	public long play(float volume, boolean isLooping) {
		long id = nativeSound.play(volume);
		setLooping(id, isLooping);

		return id;
	}

	@Override
	public void setLooping(long l, boolean b) {
		nativeSound.setLooping(l, b);
	}

	@Override
	public void setVolume(long l, float v) {
		nativeSound.setVolume(l, v);
	}

	@Override
	public void stop(long l) {
		nativeSound.stop(l);
	}

	@Override
	public void pause(long l) {
		nativeSound.pause(l);
	}

	@Override
	public void resume(long l) {
		nativeSound.resume(l);
	}
}