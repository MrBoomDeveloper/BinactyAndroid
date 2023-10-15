package com.mrboomdev.binacty.script.bridge.resources;

import com.mrboomdev.binacty.api.resources.audio.Music;

public class MyMusic extends Music {
	private final com.badlogic.gdx.audio.Music nativeMusic;

	public MyMusic(com.badlogic.gdx.audio.Music nativeMusic) {
		this.nativeMusic = nativeMusic;
	}

	@Override
	public void play() {
		nativeMusic.play();
	}

	@Override
	public void stop() {
		nativeMusic.stop();
	}

	@Override
	public void seekTo(float position) {
		nativeMusic.setPosition(position);
	}

	@Override
	public float getCurrentTime() {
		return nativeMusic.getPosition();
	}
}