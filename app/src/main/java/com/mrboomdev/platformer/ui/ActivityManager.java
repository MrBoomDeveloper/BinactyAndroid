package com.mrboomdev.platformer.ui;

import android.app.Activity;
import android.media.MediaPlayer;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mrboomdev.platformer.R;
import com.mrboomdev.platformer.ui.react.ReactActivity;

public class ActivityManager {
	public static Activity current;
	public static MediaPlayer media;
	public static ReactActivity reactActivity;
	
	public static void startMusic() {
		media = MediaPlayer.create(current, R.raw.lobby_theme);
		setVolume(current.getSharedPreferences("Save", 0).getInt("musicVolume", 100) / 100);
		media.setLooping(true);
		media.start();
	}
	
	public static void setVolume(float volume) {
		media.setVolume(volume, volume);
	}
	
	public static void resumeMusic() {
		if(media == null) return;
		media.start();
	}
	
	public static void pauseMusic() {
		if(media == null) return;
		try {
			if(!media.isPlaying()) return;
			media.pause();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stopMusic() {
		try {
			media.stop();
			media.release();
			media = null;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void gameOver() {
		reactActivity.isGameStarted = false;
		var instance = reactActivity.reactInstance;
		ReactContext context = instance.getCurrentReactContext();
		context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("GameOver", null);
	}
	
	public static void forceExit() {
		reactActivity.isGameStarted = false;
		var instance = reactActivity.reactInstance;
		ReactContext context = instance.getCurrentReactContext();
		context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("ForceExit", null);
	}
}