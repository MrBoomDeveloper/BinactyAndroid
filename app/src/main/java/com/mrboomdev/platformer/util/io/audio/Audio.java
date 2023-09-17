package com.mrboomdev.platformer.util.io.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.util.io.FileUtil;

public class Audio {
	public float volume = 1, distance = 15f;
	public boolean is3dSetup, isMusic, isStopped;
	public Vector2 position;
	public Music music;
	public Sound sound;
	private boolean isLooping;

	public Audio(Music music) {
		this.isMusic = true;
		this.music = music;
	}

	public Audio(Sound sound) {
		this.isMusic = false;
		this.sound = sound;
	}

	public Audio(FileUtil source, String path, boolean isMusic) {
		this.isMusic = isMusic;

		if(isMusic) {
			music = source.goTo(path).getLoaded(Music.class);
		} else {
			sound = source.goTo(path).getLoaded(Sound.class);
		}
	}

	public void play() {
		if(position != null) {
			setPosition(position.x, position.y);
		}

		isStopped = false;
		music.setVolume(volume);
		music.setLooping(isLooping);
		AudioUtil.playingMusic.add(music);
		AudioUtil.updateSingle(this);
		music.play();
	}

	public void stop() {
		isStopped = true;
		music.stop();
	}

	public void setLooping(boolean loop) {
		this.isLooping = loop;
		music.setLooping(loop);
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public void setVolume(float volume) {
		music.setVolume(volume);
		this.volume = volume;
	}

	public void setPosition(float x, float y) {
		if(!is3dSetup) {
			AudioUtil.activeAudio.add(this);
			position = new Vector2();
			is3dSetup = true;
		}

		position.set(x, y);
	}
}