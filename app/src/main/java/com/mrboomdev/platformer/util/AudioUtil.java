package com.mrboomdev.platformer.util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;

public class AudioUtil {
	public static float musicVolume = 1, soundVolume = 1;
	private static HashMap<String, Float> soundProgresses = new HashMap<>();
	private static Array<Music> musicQueue = new Array<>();
	
	public static void setVolume(float music, float sound) {
		musicVolume = music;
		soundVolume = sound;
	}
	
	public static void playMusic(Array<Music> queue, int repeatTimes) {
		musicQueue.addAll(queue);
		startMusic(repeatTimes);
	}
	
	private static void startMusic(int repeatTimes) {
		if(repeatTimes < 0) return;
		Music music = musicQueue.random();
		music.setOnCompletionListener(completedMusic -> {
			startMusic(repeatTimes - 1);
		});
		music.setVolume(musicVolume);
		music.setPosition(0);
		music.play();
	}
	
	public static void stopMusic() {
		for(Music music : musicQueue) {
			music.pause();
			music.stop();
		}
	}
	
	public static void playSound(String id, Sound sound, float delay) {
		
	}
	
	public static void clear() {
		stopMusic();
		musicQueue.clear();
	}
}