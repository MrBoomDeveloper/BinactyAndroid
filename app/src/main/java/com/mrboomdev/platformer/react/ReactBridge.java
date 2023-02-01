package com.mrboomdev.platformer.react;

import android.app.Activity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.mrboomdev.platformer.AndroidLauncher;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.react.ReactActivity;
import com.mrboomdev.platformer.util.Analytics;
import com.mrboomdev.platformer.util.AskUtil;
import com.mrboomdev.platformer.util.AskUtil.AskType;
import com.mrboomdev.platformer.util.StateUtil;
import android.content.Intent;

public class ReactBridge extends ReactContextBaseJavaModule {
    private MainGame game;
    private Analytics analytics;
    
    ReactBridge(ReactApplicationContext context) {
        super(context);
        game = MainGame.getInstance();
        analytics = game.analytics; //FIXME
    }

    @Override
    public String getName() {
        return "GameNative";
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
    public void play(int gamemode) {
        AndroidLauncher launcher = (AndroidLauncher)StateUtil.getActivity("Launcher");
		ReactActivity react = (ReactActivity)StateUtil.getActivity("React");
		
		Intent intent = new Intent(react, AndroidLauncher.class);
		intent.putExtra("state", "play");
		react.startActivity(intent);
    }
   
    @ReactMethod
    public void playCustom(String gamemode, String room, boolean isHost) {
        
    }
}