package com.mrboomdev.platformer.script.bridge;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.audio.AudioUtil;

@SuppressWarnings("unused")
public class AudioBridge {
	private final FileUtil source;

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
	
	public void playMusic(@NonNull String[] queue, int repeat) {
		Array<Music> musicQueue = new Array<>();
		for(String track : queue) {
			musicQueue.add(Gdx.audio.newMusic(source.goTo(track).getFileHandle()));
		}
		AudioUtil.playMusic(musicQueue, repeat);
	}
	
	public Music playMusic(String path, float volume) {
		var music = Gdx.audio.newMusic(source.goTo(path).getFileHandle());
		music.setVolume(AudioUtil.musicVolume * volume);
		music.setOnCompletionListener((completedMusic) -> AudioUtil.playingMusic.removeValue(completedMusic, false));
		music.play();
		AudioUtil.playingMusic.add(music);
		return music;
	}
	
	public void clear() {
		AudioUtil.clear();
	}
}