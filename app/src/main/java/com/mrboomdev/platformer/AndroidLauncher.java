package com.mrboomdev.platformer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.itsaky.androidide.logsender.LogSender;
import com.mrboomdev.platformer.react.ReactActivity;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;
import com.mrboomdev.platformer.util.AskUtil;
import com.mrboomdev.platformer.util.StateUtil;

public class AndroidLauncher extends AndroidApplication implements NativeContainer {
	private FirebaseCrashlytics crashlytics;
    private FirebaseAnalytics analytics;
    private AndroidApplicationConfiguration gameConfig;
    private SharedPreferences prefs;
    private boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle instance) {
        super.onCreate(instance);
        LogSender.startLogging(this);
        StateUtil.addActivity("Launcher", this);
        analytics = FirebaseAnalytics.getInstance(this);
		crashlytics = FirebaseCrashlytics.getInstance();
        prefs = getSharedPreferences("Save", 0);
		
		if(!prefs.getBoolean("crashlytics", true) || BuildConfig.DEBUG) {
        	crashlytics.setCrashlyticsCollectionEnabled(false);
		}
		Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Splash");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "SplashScreen");
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);

        gameConfig = new AndroidApplicationConfiguration();
        gameConfig.useImmersiveMode = true;
        gameConfig.useAccelerometer = false;
        gameConfig.useCompass = false;

        toggleGameView(true);
    }

    @Override
    public void toggleGameView(boolean isActive) {
        if(isActive) {
            AskUtil.setContext(this);
			
			GameSettings settings = new GameSettings()
				.setPlayerName(prefs.getString("nick", "player"))
				.setScreenInset(prefs.getInt("inset", 60))
				.setVolume(prefs.getInt("musicVolume", 100), prefs.getInt("soundsVolume", 100))
				.setDebug(prefs.getBoolean("debug", false), prefs.getBoolean("debugRenderer", false), prefs.getBoolean("debugStage", false));
			
            if(isInitialized) {
                Gdx.app.postRunnable(() -> {
					MainGame.settings = settings;
                	MainGame game = MainGame.getInstance();
                    game.setScreen(new LoadingScreen(LoadingScreen.LoadScene.GAMEPLAY));
                });
                return;
            }
            initialize(MainGame.setInstance(new AndroidAnalytics(), this, settings), gameConfig);
            isInitialized = true;
            return;
        }
        Intent intent = new Intent(this, ReactActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getStringExtra("state") == null) return;
        switch (intent.getStringExtra("state")) {
            case "nickSetupFinish":
                toggleGameView(false);
                break;
            case "play":
                toggleGameView(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {}
}