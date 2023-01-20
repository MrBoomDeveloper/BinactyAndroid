package com.mrboomdev.platformer;

import android.os.Bundle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mrboomdev.platformer.MainGame;
import com.mrboomdev.platformer.NativeContainer;
import com.itsaky.androidide.logsender.LogSender;
import com.mrboomdev.platformer.scenes.loading.LoadingScreen;
import com.mrboomdev.platformer.BuildConfig;

public class DebugStartGameActivity extends AndroidApplication implements NativeContainer {
    private FirebaseAnalytics analytics;
    private AndroidApplicationConfiguration gameConfig;
    private boolean isInitialized = false;
    
	@Override
	protected void onCreate(Bundle instance) {
		super.onCreate(instance);
		LogSender.startLogging(this);
        analytics = FirebaseAnalytics.getInstance(this);
        
		gameConfig = new AndroidApplicationConfiguration();
		gameConfig.useImmersiveMode = true;
		gameConfig.useAccelerometer = false;
		gameConfig.useCompass = false;
        
        toggleGameView(true);
	}
    
    @Override
    public void toggleGameView(boolean isActive) {
        if(isActive) {
            if(!isInitialized) {
                initialize(MainGame.getInstance(new AndroidAnalytics(), this), gameConfig);
                isInitialized = true;
                return;
            }
            Gdx.app.postRunnable(() -> {
			    MainGame.getInstance().setScreen(new LoadingScreen(LoadingScreen.LoadScene.GAMEPLAY));
            });
            return;
        }
	    Gdx.app.postRunnable(() -> {
			MainGame.getInstance().setScreen(new LoadingScreen(LoadingScreen.LoadScene.GAMEPLAY));
        });
    }
    
    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }
}