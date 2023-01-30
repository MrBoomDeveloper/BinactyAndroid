package com.mrboomdev.platformer.react;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowManager;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.soloader.SoLoader;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.react.ReactGame; 
import com.mrboomdev.platformer.util.AskUtil;
import com.facebook.react.PackageList;
import java.util.ArrayList;
import java.util.List;

public class ReactActivity extends Activity implements DefaultHardwareBackBtnHandler {
    public static ReactActivity ctx;
    private ReactRootView root;
    private ReactInstanceManager instance;

    @Override
    protected void onCreate(Bundle bundle) {
      super.onCreate(bundle);
      SoLoader.init(this, false);
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
      root.startReactApplication(instance, "GameLobbyScreen", null);
      setContentView(root);
      ctx = this;
      AskUtil.setContext(this);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
    
    
    
    public static void close() {
        ctx.finish();
    }
    
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
    
    @Override
    public void invokeDefaultOnBackPressed() {
        this.onBackPressed();
    }
}