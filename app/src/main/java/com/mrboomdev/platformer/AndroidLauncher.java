package com.mrboomdev.platformer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.itsaky.androidide.logsender.LogSender;
import com.mrboomdev.platformer.react.ReactActivity;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;
import com.mrboomdev.platformer.util.AskUtil;
import com.mrboomdev.platformer.util.StateUtil;

public class AndroidLauncher extends AndroidApplication implements NativeContainer {
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
        prefs = getSharedPreferences("Save", 0);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Splash");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "SplashScreen");
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);

        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(3600)
                        .build();
        remoteConfig.fetchAndActivate();
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings);
        Log.i("Firebase", remoteConfig.getString("beta_ui") + " beta_ui");

        gameConfig = new AndroidApplicationConfiguration();
        gameConfig.useImmersiveMode = true;
        gameConfig.useAccelerometer = false;
        gameConfig.useCompass = false;

        toggleGameView(true);
    }

    @Override
    public void toggleGameView(boolean isActive) {
        if (isActive) {
            AskUtil.setContext(this);
            if (isInitialized) {
                Gdx.app.postRunnable(() -> {
                            MainGame game = MainGame.getInstance();
                            game.nick = prefs.getString("nick", "Player");
                            game.setScreen(new LoadingScreen(LoadingScreen.LoadScene.GAMEPLAY));
                        });
                return;
            }
            initialize(MainGame.getInstance(new AndroidAnalytics(), this), gameConfig);
            isInitialized = true;
            return;
        }
        Intent intent = new Intent(this, ReactActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getStringExtra("state") == null) return;
        switch (intent.getStringExtra("state")) {
            case "nickSetupFinish":
                toggleGameView(false);
                break;
            case "play":
                toggleGameView(true);
                break;
            default:
                Log.e("AndroidLauncher", "Unknown intent");
                break;
        }
    }

    @Override
    public void onBackPressed() {}
}
