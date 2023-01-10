package com.mrboomdev.platformer;

import com.facebook.react.PackageList;
import com.facebook.react.ReactPackage;
import com.facebook.react.common.LifecycleState;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.soloader.SoLoader;

public class ReactActivity extends Activity implements DefaultHardwareBackBtnHandler {
    private ReactRootView root;
    private ReactInstanceManager instance;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    SoLoader.init(this, false);
    root = new ReactRootView(this);
    List<ReactPackage> packages = new PackageList(getApplication()).getPackages();
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
    /*Button button = new Button(this);
    button.setText("Open game");
    button.setOnClickListener(
        (View view) -> {
          finish();
          MainGame.getInstance().toggleGameView(true);
        });
    setContentView(button);*/
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  @Override
  public void invokeDefaultOnBackPressed() {
      this.onBackPressed();
  }
}