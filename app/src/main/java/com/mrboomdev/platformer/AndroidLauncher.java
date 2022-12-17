package com.mrboomdev.platformer;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.mrboomdev.platformer.AndroidAnalytics;
import com.itsaky.androidide.logsender.LogSender;

public class AndroidLauncher extends AndroidApplication {
    private FirebaseAnalytics analytics;
    
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
		
		AndroidApplicationConfiguration gameConfig = new AndroidApplicationConfiguration();
		gameConfig.useImmersiveMode = true;
		gameConfig.useAccelerometer = false;
		gameConfig.useCompass = false;
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		initialize(new MainGame(new AndroidAnalytics()), gameConfig);
	}
    
    @Override
    public void onBackPressed() {
        
    }
}