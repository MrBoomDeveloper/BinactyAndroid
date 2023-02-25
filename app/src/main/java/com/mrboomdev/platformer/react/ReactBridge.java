package com.mrboomdev.platformer.react;

import android.content.SharedPreferences;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.util.AskUtil;
import com.mrboomdev.platformer.util.StateUtil;
import android.content.Intent;

public class ReactBridge extends ReactContextBaseJavaModule {
    
    ReactBridge(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "GameNative";
    }
	
	@ReactMethod
	public void setKey(String key, String value, String type) {
		SharedPreferences prefs = StateUtil.getActivity("React").getSharedPreferences("Save", 0);
		switch(type) {
			case "string":
				prefs.edit().putString(key, value).commit();
				break;
			case "number":
				prefs.edit().putInt(key, Integer.parseInt(value)).commit();
				break;
			case "boolean":
				prefs.edit().putBoolean(key, Boolean.parseBoolean(value)).commit();
				break;
		}
	}
	
	@ReactMethod
	public void getKeys(ReadableArray keys, Callback callback) {
		SharedPreferences prefs = StateUtil.getActivity("React").getSharedPreferences("Save", 0);
		WritableArray result = Arguments.createArray();
		for(int i = 0; i < keys.size(); i++) {
			ReadableMap was = keys.getMap(i);
			WritableMap newMap = Arguments.createMap();
			String key = was.getString("id");
			
			newMap.putString("key", key);
			newMap.putString("id", key);
			newMap.putString("title", was.getString("title"));
			newMap.putString("type", was.getString("type"));
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
		callback.invoke(result);
	}
	
	@ReactMethod
	public void getMyData(Callback callback) {
		WritableMap data = Arguments.createMap();
		if(!AskUtil.prefs.getBoolean("isNickSetup", false)) {
			data.putString("nick", "Player");
        	data.putString("avatar", "klarrie");
        	data.putInt("level", 1);
        	data.putInt("progress", 0);
		} else {
			data.putString("nick", "MrBoomDev");
        	data.putString("avatar", "klarrie");
        	data.putInt("level", 1);
        	data.putInt("progress", 0);
		}
        callback.invoke(data);
	}
	
    @ReactMethod
    public void getPlayerData(String nick, Callback callback) {
        WritableMap data = Arguments.createMap();
		data.putString("nick", "Unknown");
        data.putString("avatar", "error");
        data.putInt("level", 0);
        data.putInt("progress", 0);
        callback.invoke(data);
    }
	
	@ReactMethod
	public void requestClose() {
		AskUtil.ask(AskUtil.AskType.REQUEST_CLOSE, obj -> {
			StateUtil.getActivity("React").finishAffinity();
		});
	}
    
    @ReactMethod
    public void play(int gamemode) {
		ReactActivity react = (ReactActivity)StateUtil.getActivity("React");
		
		Intent intent = new Intent(react, GameLauncher.class);
		intent.putExtra("state", "play");
		react.startActivity(intent);
    }
   
    @ReactMethod
    public void playCustom(String gamemode, String room, boolean isHost) {
        
    }
}