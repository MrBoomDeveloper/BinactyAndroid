package com.mrboomdev.platformer;

import android.os.Bundle;
import android.util.DisplayMetrics;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.itsaky.androidide.logsender.LogSender;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogSender.startLogging(this);
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		config.useAccelerometer = false;
		config.useCompass = false;
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		initialize(new MainGame(), config);
		
		//initialize(new GameplayScene(metrics.widthPixels, metrics.heightPixels), config);
	}
    
    @Override
    public void onBackPressed() {
        
    }
}


