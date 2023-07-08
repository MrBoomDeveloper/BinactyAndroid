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
	private static boolean isMusicPlaying, isActivityResumed;

	public static void onPause() {
		if(isMusicPlaying) {
			pauseMusic();
			isMusicPlaying = true;
		}

		isActivityResumed = false;
	}

	public static void onResume() {
		isActivityResumed = true;
		if(isMusicPlaying) resumeMusic();
	}
	
	public static void startMusic() {
		isMusicPlaying = true;
		if(reactActivity.isGameStarted || isPlaying()) return;
		media = MediaPlayer.create(current, R.raw.lobby_theme);

		setVolume(current.getSharedPreferences("Save", 0).getInt("musicVolume", 100) / 100f);
		media.setLooping(true);

		if(!isActivityResumed) return;
		media.start();
	}

	public static boolean isPlaying() {
		if(media == null) return false;

		try {
			return media.isPlaying();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
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
		if(media == null || reactActivity.isGameStarted || !isActivityResumed) return;

		try {
			media.start();
			isMusicPlaying = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void pauseMusic() {
		if(media == null) return;

		try {
			if(!media.isPlaying()) return;
			media.pause();
			isMusicPlaying = false;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stopMusic() {
		try {
			media.stop();
			media.release();
			media = null;
			isMusicPlaying = false;
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