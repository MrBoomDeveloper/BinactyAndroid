package com.mrboomdev.platformer.script.bridge;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.AudioUtil;
import com.mrboomdev.platformer.util.io.FileUtil;

public class AudioBridge {
	private GameHolder game = GameHolder.getInstance();
	private FileUtil source;
	
	public AudioBridge(FileUtil source) {
		this.source = source;
	}
	
	public void playSound(String path, float volume) {
		var sound = source.goTo(path).getLoaded(Sound.class);
		sound.play(volume * AudioUtil.soundVolume);
	}
	
	public void playSound(String path, float volume, float power, Vector2 position) {
		var sound = source.goTo(path).getLoaded(Sound.class);
		AudioUtil.play3DSound(sound, volume, power, position);
	}
	
	public void playMusic(String[] queue, int repeat) {
		Array<Music> musicQueue = new Array<>();
		for(String track : queue) {
			musicQueue.add(source.goTo(track).getLoaded(Music.class));
		}
		AudioUtil.playMusic(musicQueue, repeat);
	}
}