package com.mrboomdev.platformer.script.bridge;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.util.AudioUtil;

public class AudioBridge {
	private GameHolder game = GameHolder.getInstance();
	
	public void playSound(String path) {
		
	}
	
	public void playMusic(String[] queue, int repeat) {
		Array<Music> musicQueue = new Array<>();
		for(String track : queue) {
			musicQueue.add(game.assets.get("packs/fnaf/" + track));
		}
		AudioUtil.playMusic(musicQueue, repeat);
	}
}