package com.mrboomdev.platformer.ui.react.bridge;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.mrboomdev.platformer.ui.ActivityManager;

public class AppBridge extends ReactContextBaseJavaModule {
	
	public AppBridge(ReactApplicationContext context) {
		super(context);
	}

    @Override
    public String getName() {
		return "AppBridge";
	}
	
	@ReactMethod
	public void startMusic() {
		ActivityManager.startMusic();
	}
	
	@ReactMethod
	public void stopMusic() {
		ActivityManager.stopMusic();
	}
	
	@ReactMethod
	public void setVolume(double volume) {
		ActivityManager.setVolume((float)(volume / 100));
	}
}