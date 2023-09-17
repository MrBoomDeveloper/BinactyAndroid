package com.mrboomdev.platformer.ui.react;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("unused")
@Deprecated
public class ReactBridge extends ReactContextBaseJavaModule {
    public ReactBridge(ReactApplicationContext context) {
        super(context);
    }

    @NonNull
	@Override
    public String getName() {
        return "GameNative";
    }
	
	@ReactMethod
	public void addListener(String name) {}
	
	@ReactMethod
	public void removeListeners(Integer count) {}
	
	@ReactMethod
	public void finish(String activity) {
		if(ActivityManager.current instanceof ReactActivity) return;
		ActivityManager.current.finish();
	}
    
    @ReactMethod
    public void play(ReadableMap data) {
		var activity = ActivityManager.reactActivity;
		if(activity.isGameStarted) return;
		activity.isGameStarted = true;

		Intent intent = new Intent(activity, GameLauncher.class);
		intent.putExtra("enableEditor", data.getBoolean("enableEditor"));

		var jsLevel = data.getMap("level");
		if(jsLevel != null) {
			Bundle levelBundle = new Bundle();
			levelBundle.putString("name", jsLevel.getString("name"));
			levelBundle.putString("id", jsLevel.getString("id"));
			intent.putExtra("level", levelBundle);
		}

		if(data.hasKey("entry")) {
			try {
				var moshi = new Moshi.Builder().build();
				var adapter = moshi.adapter(PackData.GamemodeEntry.class);
				var entry = adapter.fromJson(Objects.requireNonNull(data.getString("entry")));
				intent.putExtra("gamemodeFile", Objects.requireNonNull(entry).file);
				intent.putExtra("engine", entry.engine);
				intent.putExtra("version", entry.version);
				intent.putExtra("main", entry.main);
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else {
			intent.putExtra("gamemodeFile", data.getString("file"));
			intent.putExtra("mapFile", data.getString("mapFile"));
		}

		activity.startActivity(intent);
		ActivityManager.stopMusic();
    }
}