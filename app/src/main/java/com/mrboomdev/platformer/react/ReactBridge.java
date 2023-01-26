package com.mrboomdev.platformer.react;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.react.ReactActivity;
import com.mrboomdev.platformer.util.Analytics;
import com.mrboomdev.platformer.util.AskUtil;
import com.mrboomdev.platformer.util.AskUtil.AskType;
import java.util.HashMap;
import java.util.Map;

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
    public Map<String, Object> getPlayerData(String nick) {
        Map<String, Object> data = new HashMap<>();
        data.put("nick", "Player228");
        data.put("avatar", "klarrie");
        data.put("level", 1);
        data.put("progress", 0);
        return data;
    }
    
    @ReactMethod
    public void play(int gamemode) {
        
    }
    
    @ReactMethod
    public void playCustom(String gamemode, String room, boolean isHost) {
        
    }
    
    @ReactMethod
    public void setupNick(Promise promise) {
        AskUtil.ask(AskType.SETUP_NICK, promise);
    }
}