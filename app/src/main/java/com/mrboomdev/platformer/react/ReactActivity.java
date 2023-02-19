package com.mrboomdev.platformer.react;

import androidx.appcompat.app.AppCompatActivity;
import com.mrboomdev.platformer.AndroidLauncher;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.soloader.SoLoader;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.util.AskUtil;
import com.facebook.react.PackageList;
import com.mrboomdev.platformer.util.StateUtil;
import com.mrboomdev.platformer.AndroidLauncher;
import java.util.List;

public class ReactActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {
    private ReactRootView root;
    private ReactInstanceManager instance;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        StateUtil.addActivity("React", this);
        SoLoader.init(this, false);
        AskUtil.setContext(this);

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
        getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        prefs = getSharedPreferences("Save", 0);
        if(!prefs.getBoolean("isNickSetup", false)) {
            AskUtil.ask(AskUtil.AskType.SETUP_NICK, (data) -> {
            	prefs.edit().putBoolean("isNickSetup", true).commit();
                Intent intent = new Intent(this, AndroidLauncher.class);
                intent.putExtra("state", "nickSetupFinish");
                startActivity(intent);
            });
        }
    }

    @Override
    public void invokeDefaultOnBackPressed() {
		StateUtil.getActivity("Launcher").finish();
		finish();
	}
	
	@Override
	public void onBackPressed() {
		if(instance != null) {
			instance.onBackPressed();
		} else {
			invokeDefaultOnBackPressed();
		}
	}
}