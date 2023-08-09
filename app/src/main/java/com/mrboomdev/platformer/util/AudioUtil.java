package com.mrboomdev.platformer.util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.game.GameHolder;

public class AudioUtil {
	public static float musicVolume = 1, soundVolume = 1;
	public static Array<Music> playingMusic = new Array<>();
	private static final Array<Music> musicQueue = new Array<>();
	private static Music currentTheme;
	
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
		currentTheme = musicQueue.random();
		currentTheme.setOnCompletionListener(completedMusic -> startMusic(repeatTimes - 1));
		currentTheme.setVolume(.1f * musicVolume);
		currentTheme.setPosition(0);
		currentTheme.play();
	}
	
	public static void stopMusic() {
		for(Music music : musicQueue) {
			music.pause();
			music.stop();
		}
		
		for(Music music : playingMusic) {
			music.pause();
			music.stop();
		}
	}
	
	public static void play3DSound(Sound sound, float volume, float power, Vector2 position) {
		float resultVolume = getVolume(position, power) * volume * soundVolume;
		if(resultVolume <= 0) return;
		sound.play(resultVolume);
	}
	
	public static float getVolume(Vector2 position, float power) {
		float distance = 0;
		var game = GameHolder.getInstance();
		if(game.settings.mainPlayer != null) {
			var playerPosition = game.settings.mainPlayer.body.getPosition();
			distance = position.dst(playerPosition);
		}
		
		float result = 1 - (distance / power);
		return Math.max(result, 0);
	}
	
	public static void pause() {
		if(currentTheme != null) currentTheme.pause();
		for(Music music : playingMusic) {
			music.pause();
		}
	}
	
	public static void resume() {
		if(currentTheme != null) currentTheme.play();
		for(Music music : playingMusic) {
			music.play();
		}
	}
	
	public static void clear() {
		stopMusic();
		musicQueue.clear();
		playingMusic.clear();
		if(currentTheme != null) currentTheme.stop();
	}
}