package com.mrboomdev.platformer.ui.react;

import android.content.Intent;

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
import com.mrboomdev.platformer.game.pack.PackLoader;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@SuppressWarnings("unused")
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
			
			switch(was.getString("type")) {
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
    public void getPlayerData(String nick, @NonNull Promise promise) {
        WritableMap data = Arguments.createMap();
		data.putString("nick", "Unknown");
        data.putString("avatar", "error");
        data.putInt("level", 0);
        data.putInt("progress", 0);
        promise.resolve(data);
    }
	
	@ReactMethod
	public void getGamemodes(Promise promise) {
		var jsGamemodes = Arguments.createArray();
		for(var row : PackLoader.getGamemodes()) {
			var jsRow = Arguments.createMap();
			jsRow.putString("title", row.title);
			jsRow.putString("id", row.id);
			var jsData = Arguments.createArray();
			for(var gamemode : row.data) {
				var jsGamemode = Arguments.createMap();
				jsGamemode.putString("name", gamemode.name);
				jsGamemode.putString("id", gamemode.id);
				
				Moshi moshi = new Moshi.Builder().build();
				JsonAdapter<FileUtil> adapter = moshi.adapter(FileUtil.class);
				jsGamemode.putString("file", adapter.toJson(gamemode.file));
				
				jsGamemode.putInt("maxPlayers", gamemode.maxPlayers);
				if(gamemode.author != null) jsGamemode.putString("author", gamemode.author.name);
				if(gamemode.time != null) jsGamemode.putString("time", gamemode.time);
				if(gamemode.description != null) jsGamemode.putString("description", gamemode.description);
				if(gamemode.banner != null) jsGamemode.putString("banner", gamemode.source.goTo(gamemode.banner).getFullPath(true));
				if(gamemode.type == null) gamemode.type = "match";
				switch(gamemode.type) {
					case "match": {
						var jsMaps = Arguments.createArray();
						for(var map : gamemode.maps) {
							var jsMap = Arguments.createMap();
							jsMap.putString("name", map.name);
							jsMap.putString("author", map.author.name);
							jsMap.putString("file", adapter.toJson(gamemode.source.goTo(map.file.getPath())));
							jsMaps.pushMap(jsMap);
						}
						jsGamemode.putArray("maps", jsMaps);
						break;
					}
					case "story": {
						break;
					}
				}
				jsData.pushMap(jsGamemode);
			}
			jsRow.putArray("data", jsData);
			jsGamemodes.pushMap(jsRow);
		}
		promise.resolve(jsGamemodes);
	}
	
	@ReactMethod
	public void getMissions(@NonNull Promise promise) {
		WritableArray missions = Arguments.createArray();
		WritableMap mission = Arguments.createMap();
		mission.putString("name", "Find The Capybara");
		mission.putString("description", "The Legendary Animal is hidden somewhere. Find it!");
		mission.putDouble("progress", 0);
		mission.putDouble("progressMax", 1);
		promise.resolve(missions);
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
	public void requestClose() {
		var dialog = new AndroidDialog().setTitle("Confirm Exit");
		dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT).setTextColor("#ffffff").setText("Are you sure, you want to exit the game?"));
		dialog.addAction(new AndroidDialog.Action().setText("Cancel").setClickListener(button -> dialog.close()));
		dialog.addAction(new AndroidDialog.Action().setText("Exit").setClickListener(button -> {
			ActivityManager.current.finishAffinity();
			dialog.close();
		}));
		dialog.addSpace(30).show();
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
		var activity = (ReactActivity)ActivityManager.current;
		if(activity.isGameStarted) return;
		activity.isGameStarted = true;
		Intent intent = new Intent(activity, GameLauncher.class);
		intent.putExtra("enableEditor", data.getBoolean("enableEditor"));
		intent.putExtra("gamemodeFile", data.getString("file"));
		intent.putExtra("mapFile", data.getString("mapFile"));
		activity.startActivity(intent);
		ActivityManager.stopMusic();
    }
}