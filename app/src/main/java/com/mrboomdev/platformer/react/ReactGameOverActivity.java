package com.mrboomdev.platformer.react;

import android.os.Bundle;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.react.PackageList;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.util.StateUtil;
import java.util.List;

public class ReactGameOverActivity extends AppCompatActivity {
	private ReactRootView root;
    private ReactInstanceManager instance;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        StateUtil.addActivity("GameOver", this);

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
	
	@Override
	public void onBackPressed() {
		finish();
	}
}