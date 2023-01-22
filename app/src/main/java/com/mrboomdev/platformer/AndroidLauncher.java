package com.mrboomdev.platformer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;
import android.widget.EditText;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.itsaky.androidide.logsender.LogSender;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;

public class AndroidLauncher extends AndroidApplication implements NativeContainer {
    private FirebaseAnalytics analytics;
    private AndroidApplicationConfiguration gameConfig;
    private boolean isInitialized = false;
    
	@Override
	protected void onCreate(Bundle instance) {
		super.onCreate(instance);
		LogSender.startLogging(this);
        analytics = FirebaseAnalytics.getInstance(this);
        
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Splash");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "SplashScreen");
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
        
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build();
        remoteConfig.fetchAndActivate();
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings);
        Log.i("Firebase", remoteConfig.getString("beta_ui"));
		
		gameConfig = new AndroidApplicationConfiguration();
		gameConfig.useImmersiveMode = true;
		gameConfig.useAccelerometer = false;
		gameConfig.useCompass = false;
        
        toggleGameView(true);
	}
    
    @Override
    public void toggleGameView(boolean isActive) {
        if(isActive) {
            if(isInitialized) {
                Gdx.app.postRunnable(() -> {
                    MainGame.getInstance().setScreen(new LoadingScreen(LoadingScreen.LoadScene.GAMEPLAY));
                });
            } else {
                initialize(MainGame.getInstance(new AndroidAnalytics(), this), gameConfig);
                isInitialized = true;
            }
        } else {
            Intent intent = new Intent(this, ReactActivity.class);
            startActivity(intent);
        }
    }
    
    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }
    
    @Override
    public void onBackPressed() {}
}