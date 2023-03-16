package com.mrboomdev.platformer.react;

import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.react.PackageList;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.ui.react.ReactGame;
import java.util.List;

public class ReactGameOverActivity extends AppCompatActivity {
	public static ReactGameOverActivity activity;
	private ReactRootView root;
    private ReactInstanceManager instance;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		activity = this;
		
        root = new ReactRootView(this);
        List<ReactPackage> packages = new PackageList(getApplication()).getPackages();
        packages.add(new ReactGame());
        instance = ReactInstanceManager.builder()
        	.setApplication(getApplication())
            .setCurrentActivity(this)
            .setBundleAssetName("index.android.bundle")
            .setJSMainModulePath("index")
            .addPackages(packages)
            .setUseDeveloperSupport(BuildConfig.DEBUG)
            .setInitialLifecycleState(LifecycleState.RESUMED)
            .build();
		
        root.startReactApplication(instance, "GameOverScreen", null);
        setContentView(root);
        getWindow().getInsetsController().hide(WindowInsets.Type.navigationBars());
    }
}