package com.mrboomdev.platformer;

import android.os.Bundle;
import android.util.DisplayMetrics;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		config.useAccelerometer = false;
		config.useCompass = false;
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		initialize(new GameplayScene(metrics.widthPixels, metrics.heightPixels), config);
	}
    
    @Override
    public void onBackPressed() {
        
    }
}


