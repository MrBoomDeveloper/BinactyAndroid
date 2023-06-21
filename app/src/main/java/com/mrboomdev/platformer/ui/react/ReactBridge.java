package com.mrboomdev.platformer.ui.react;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.mrboomdev.platformer.game.GameHolder;
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
	@Deprecated
	public void setKey(@NonNull String type, String key, String value) {
		var prefs = ActivityManager.current.getSharedPreferences("Save", 0);
		switch(type) {
			case "string":
				prefs.edit().putString(key, value).apply();
				break;
			case "int":
				prefs.edit().putInt(key, Integer.parseInt(value)).apply();
				break;
			case "float":
				prefs.edit().putFloat(key, Float.parseFloat(value)).apply();
				break;
			case "boolean":
				prefs.edit().putBoolean(key, Boolean.parseBoolean(value)).apply();
				break;
		}
	}
	
	@ReactMethod
	@Deprecated
	public void getKey(@NonNull String type, String key, Promise promise) {
		var prefs = ActivityManager.current.getSharedPreferences("Save", 0);
		switch(type) {
			case "string":
				promise.resolve(prefs.getString(key, ""));
				break;
			case "int":
				promise.resolve(prefs.getInt(key, 0));
				break;
			case "float":
				promise.resolve(prefs.getFloat(key, 0));
				break;
			case "boolean":
				promise.resolve(prefs.getBoolean(key, false));
				break;
		}
	}
	
	@ReactMethod
	@Deprecated
	public void getKeys(@NonNull ReadableArray keys, Promise promise) {
		var prefs = ActivityManager.current.getSharedPreferences("Save", 0);
		WritableArray result = Arguments.createArray();
		for(int i = 0; i < keys.size(); i++) {
			ReadableMap was = keys.getMap(i);
			WritableMap newMap = Arguments.createMap();
			String key = was.getString("id");
			
			newMap.putString("key", key);
			newMap.putString("id", key);
			newMap.putString("title", was.getString("title"));
			newMap.putString("type", was.getString("type"));
			if(was.hasKey("max")) newMap.putInt("max", was.getInt("max"));
			if(was.hasKey("description")) newMap.putString("description", was.getString("description"));
			
			switch(Objects.requireNonNullElse(was.getString("type"), "string")) {
				case "string":
					newMap.putString("initial", prefs.getString(key, was.getString("initial")));
					break;

				case "number":
					newMap.putInt("initial", prefs.getInt(key, was.getInt("initial")));
					break;

				case "boolean":
					newMap.putBoolean("initial", prefs.getBoolean(key, was.getBoolean("initial")));
					break;
			}
			result.pushMap(newMap);
		}
		promise.resolve(result);
	}
	
	@ReactMethod
	public void getStats(Promise promise) {
		try {
			var stats = GameHolder.getInstance().stats;
			WritableMap map = Arguments.createMap();
			map.putBoolean("isWin", stats.isWin);
			map.putDouble("totalKills", stats.totalKills);
			map.putDouble("totalDamage", stats.totalDamage);
			map.putDouble("matchDuration", stats.matchDuration);
			promise.resolve(map);
		} catch(Exception e) {
			promise.reject("Failed to collect stats", e);
			e.printStackTrace();
		}
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