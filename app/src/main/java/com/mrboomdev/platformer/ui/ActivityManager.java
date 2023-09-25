package com.mrboomdev.platformer.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mrboomdev.binacty.rn.RNActivity;
import com.mrboomdev.binacty.rn.RNApp;
import com.mrboomdev.platformer.R;

import java.util.Objects;

@SuppressLint({"VisibleForTests", "StaticFieldLeak"})
public class ActivityManager {
	public static Activity current;
	public static MediaPlayer media;
	public static RNActivity reactActivity;
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
		media.setLooping(true);
		setVolume(RNApp.getSave("Save").getInt("musicVolume", 100) / 100f);

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
		var instance = RNApp.getReactInstance();
		ReactContext context = Objects.requireNonNull(instance.getCurrentReactContext());
		context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("GameOver", null);
	}
	
	public static void forceExit() {
		reactActivity.isGameStarted = false;
		var instance = RNApp.getReactInstance();
		ReactContext context = Objects.requireNonNull(instance.getCurrentReactContext());
		context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("ForceExit", null);
	}
	
	public static void toast(String text, boolean isLong) {
		current.runOnUiThread(() -> Toast.makeText(current.getApplication(), text, (isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show());
	}

	public static void hideSystemUi(@NonNull Activity activity) {
		var window = activity.getWindow();
		var params = new WindowManager.LayoutParams();

		WindowCompat.setDecorFitsSystemWindows(window, true);
		var windowController = WindowCompat.getInsetsController(window, window.getDecorView());
		windowController.hide(WindowInsetsCompat.Type.systemBars());
		windowController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
		}

		window.setAttributes(params);
		window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	}
}