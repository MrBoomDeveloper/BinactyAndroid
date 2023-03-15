package com.mrboomdev.platformer.ui.react;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.react.PackageList;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.soloader.SoLoader;
import com.itsaky.androidide.logsender.LogSender;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.react.ReactGame;
import com.mrboomdev.platformer.util.AskUtil;
import java.util.List;

public class ReactActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {
	public static ReactActivity instance;
    private ReactRootView root;
    private ReactInstanceManager reactInstance;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle bundle) {
		LogSender.startLogging(this);
        super.onCreate(bundle);
        SoLoader.init(this, false);
		AskUtil.setContext(this);
		
		instance = this;
        root = new ReactRootView(this);
        List<ReactPackage> packages = new PackageList(getApplication()).getPackages();
        packages.add(new ReactGame());
        reactInstance = ReactInstanceManager.builder()
        	.setApplication(getApplication())
            .setCurrentActivity(this)
            .setBundleAssetName("index.android.bundle")
            .setJSMainModulePath("index")
            .addPackages(packages)
            .setUseDeveloperSupport(BuildConfig.DEBUG)
            .setInitialLifecycleState(LifecycleState.RESUMED)
            .build();
		
        root.startReactApplication(reactInstance, "GameLobbyScreen", null);
        setContentView(root);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		getWindow().getInsetsController().hide(WindowInsets.Type.navigationBars());

        prefs = getSharedPreferences("Save", 0);
        if(!prefs.getBoolean("isNickSetup", false)) {
            AskUtil.ask(AskUtil.AskType.SETUP_NICK, (data) -> {
            	prefs.edit().putBoolean("isNickSetup", true).commit();
				Intent intent = new Intent(this, ReactActivity.class);
				startActivity(intent);
				finish();
            });
        }
    }

    @Override
    public void invokeDefaultOnBackPressed() {
		finishAffinity();
	}
	
	@Override
	public void onBackPressed() {
		instance.onBackPressed();
	}
}