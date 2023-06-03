package com.mrboomdev.platformer.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.widget.Toast;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mrboomdev.platformer.R;
import com.mrboomdev.platformer.ui.react.ReactActivity;

import java.util.Objects;

@SuppressLint({"VisibleForTests", "StaticFieldLeak"})
public class ActivityManager {
	public static Activity current;
	public static MediaPlayer media;
	public static ReactActivity reactActivity;
	
	public static void startMusic() {
		if(reactActivity.isGameStarted) return;
		stopMusic();
		media = MediaPlayer.create(current, R.raw.lobby_theme);
		setVolume(current.getSharedPreferences("Save", 0).getInt("musicVolume", 100) / 100f);
		media.setLooping(true);
		media.start();
	}
	
	public static void setVolume(float volume) {
		if(media == null) return;
		try {
			media.setVolume(volume, volume);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void resumeMusic() {
		if(media == null || reactActivity.isGameStarted) return;
		try {
			media.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
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
		Objects.requireNonNull(context).getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("GameOver", null);
	}
	
	public static void forceExit() {
		reactActivity.isGameStarted = false;
		var instance = reactActivity.reactInstance;
		ReactContext context = instance.getCurrentReactContext();
		Objects.requireNonNull(context).getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("ForceExit", null);
	}
	
	public static void toast(String text, boolean isLong) {
		current.runOnUiThread(() -> Toast.makeText(current.getApplication(), text, (isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show());
	}
}