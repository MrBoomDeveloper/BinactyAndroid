package com.mrboomdev.platformer.ui.react;

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
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.AskUtil;
import android.content.Intent;

public class ReactBridge extends ReactContextBaseJavaModule {
    public ReactBridge(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "GameNative";
    }
	
	@ReactMethod
	public void setKey(String type, String key, String value) {
		var prefs = ActivityManager.current.getSharedPreferences("Save", 0);
		switch(type) {
			case "string":
				prefs.edit().putString(key, value).commit();
				break;
			case "int":
				prefs.edit().putInt(key, Integer.parseInt(value)).commit();
				break;
			case "float":
				prefs.edit().putFloat(key, Float.parseFloat(value)).commit();
				break;
			case "boolean":
				prefs.edit().putBoolean(key, Boolean.parseBoolean(value)).commit();
				break;
		}
	}
	
	@ReactMethod
	public void getKey(String type, String key, Promise promise) {
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
	public void getKeys(ReadableArray keys, Promise promise) {
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
	public void getMyData(Promise promise) {
		var prefs = ActivityManager.current.getSharedPreferences("Save", 0);
		WritableMap data = Arguments.createMap();
		data.putString("nick", prefs.getString("nick", "Player"));
    	data.putString("avatar", "klarrie");
        data.putInt("level", 1);
        data.putInt("progress", 0);
        promise.resolve(data);
	}
	
    @ReactMethod
    public void getPlayerData(String nick, Promise promise) {
        WritableMap data = Arguments.createMap();
		data.putString("nick", "Unknown");
        data.putString("avatar", "error");
        data.putInt("level", 0);
        data.putInt("progress", 0);
        promise.resolve(data);
    }
	
	@ReactMethod
	public void getGamemodes(Promise promise) {
		WritableArray array = Arguments.createArray();
		
		WritableMap gamemodes = Arguments.createMap();
		WritableArray gamemodesData = Arguments.createArray();
		gamemodes.putString("title", "Gamemodes");
		
		WritableMap other = Arguments.createMap();
		WritableArray otherData = Arguments.createArray();
		other.putString("title", "Other");
		
		for(int i = 1; i < 5; i++) {
			WritableMap fnaf = Arguments.createMap();
			fnaf.putString("name", "Five Nights at Freddy's " + i);
			fnaf.putString("description", "Welcome to your new summer job at Freddy Fazbear's Pizza, where kids and parents alike come for entertainment and food!");
			fnaf.putString("id", "fnaf" + i);
			fnaf.putString("author", "MrBoomDev");
			gamemodesData.pushMap(fnaf);
		}
		
		for(int i = 1; i < 5; i++) {
			WritableMap test = Arguments.createMap();
			test.putString("name", "Test gamemode #" + i);
			test.putString("id", "halloween" + i);
			test.putString("author", "MrBoomDev");
			gamemodesData.pushMap(test);
		}
		
		WritableMap test = Arguments.createMap();
		test.putString("name", "Tutorial");
		test.putString("id", "tutorial");
		test.putString("author", "MrBoomDev");
		otherData.pushMap(test);
		
		gamemodes.putArray("data", gamemodesData);
		other.putArray("data", otherData);
		array.pushMap(gamemodes);
		array.pushMap(other);
		promise.resolve(array);
	}
	
	@ReactMethod
	public void getMissions(Promise promise) {
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
	public void getPacks(Promise promise) {
		
	}
	
	@ReactMethod
	public void startMusic() {
		ActivityManager.startMusic();
	}
	
	@ReactMethod
	public void requestClose() {
		AskUtil.ask(AskUtil.AskType.REQUEST_CLOSE, obj -> {
			ActivityManager.current.finishAffinity();
		});
	}
	
	@ReactMethod
	public void addListener(String name) {}
	
	@ReactMethod
	public void removeListeners(Integer count) {}
	
	@ReactMethod
	public void managePacks() {
		var dialog = new AndroidDialog().setTitle("Manage Your Packs");
		dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT).setText("Sorry, but this feature is currently unavailable."));
		dialog.addAction(new AndroidDialog.Action().setText("Cancel").setClickListener(button -> dialog.close()));
		dialog.addAction(new AndroidDialog.Action().setText("Save").setClickListener(button -> {
			dialog.close();
		}));
		dialog.addAction(new AndroidDialog.Action().setText("Import").setClickListener(button -> {
			
		}));
		dialog.addSpace(30).show();
	}
	
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
		if(data.hasKey("gamemodePath")) intent.putExtra("gamemodePath", data.getBoolean("gamemodePath"));
		if(data.hasKey("gamemodeSource")) intent.putExtra("gamemodeSource", data.getBoolean("gamemodeSource"));
		if(data.hasKey("mapPath")) intent.putExtra("mapPath", data.getBoolean("mapPath"));
		if(data.hasKey("mapSource")) intent.putExtra("mapSource", data.getBoolean("mapSource"));
		activity.startActivity(intent);
		ActivityManager.stopMusic();
    }
}