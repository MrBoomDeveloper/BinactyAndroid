package com.mrboomdev.platformer.ui.react;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.facebook.react.PackageList;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.soloader.SoLoader;
import com.itsaky.androidide.logsender.LogSender;
import com.mrboomdev.platformer.*;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.AskUtil;
import com.mrboomdev.platformer.util.FunUtil;
import com.mrboomdev.platformer.util.io.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReactActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {
    public static ReactActivity instance;
    public ReactInstanceManager reactInstance;
    private ReactRootView root;
    private SharedPreferences prefs;
	private long lastBackPressed;
	public boolean isGameStarted;

    @Override
    protected void onCreate(Bundle bundle) {
        LogSender.startLogging(this);
        super.onCreate(bundle);
        SoLoader.init(this, false);
        ActivityManager.current = this;
        ActivityManager.reactActivity = this;

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

        root.startReactApplication(reactInstance, "App", null);
        setContentView(root);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        var windowController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowController.hide(WindowInsetsCompat.Type.systemBars());
        windowController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        prefs = getSharedPreferences("Save", 0);
        if (!prefs.getBoolean("isNickSetup", false)) {
            AskUtil.ask(
                    AskUtil.AskType.SETUP_NICK,
                    (data) -> {
                        prefs.edit().putBoolean("isNickSetup", true).commit();
                        prefs.edit().putString("nick", (String) data).commit();
                        Intent intent = new Intent(this, ReactActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }

        if(!prefs.getBoolean("isPacksListDefaultCopied", false)) {
			FileUtil.external("packs/installed.json").writeString(FileUtil.internal("packs/defaultList.json").readString(false), false);
            prefs.edit().putBoolean("isPacksListDefaultCopied", true).apply();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ActivityManager.pauseMusic();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityManager.current = this;
        ActivityManager.resumeMusic();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ActivityManager.stopMusic();
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        finishAffinity();
    }

    @Override
    public void onBackPressed() {
		if(Instant.now().getEpochSecond() < lastBackPressed + 1) return;
		lastBackPressed = Instant.now().getEpochSecond();
		instance.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch(requestCode) {
			case 1: {
				if(resultCode != Activity.RESULT_OK || resultData == null) return;
				Uri uri = resultData.getData();
				break;
			}
		}
    }
}