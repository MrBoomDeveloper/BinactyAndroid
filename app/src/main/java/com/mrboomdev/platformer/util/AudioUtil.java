package com.mrboomdev.platformer.util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.game.GameHolder;

public class AudioUtil {
	public static float musicVolume = 1, soundVolume = 1;
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
		music.setVolume(0.5f * musicVolume);
		music.setPosition(0);
		music.play();
	}
	
	public static void stopMusic() {
		for(Music music : musicQueue) {
			music.pause();
			music.stop();
		}
	}
	
	public static void play3DSound(Sound sound, float volume, float power, Vector2 position) {
		float resultVolume = getVolume(position, power) * volume * soundVolume;
		if(resultVolume <= 0) return;
		sound.play(resultVolume);
	}
	
	private static float getVolume(Vector2 position, float power) {
		float distance = 0;
		var game = GameHolder.getInstance();
		if(game.settings.mainPlayer != null) {
			var playerPosition = game.settings.mainPlayer.body.getPosition();
			distance = position.dst(playerPosition);
		}
		
		float result = 1 - (distance / power);
		return Math.max(result, 0);
	}
	
	public static void clear() {
		stopMusic();
		musicQueue.clear();
	}
}