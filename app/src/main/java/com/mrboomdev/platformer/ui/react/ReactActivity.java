package com.mrboomdev.platformer.ui.react;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
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
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class ReactActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {
	public static ReactActivity instance;
	public MediaPlayer media;
    private ReactRootView root;
    private ReactInstanceManager reactInstance;
    private SharedPreferences prefs;
	public boolean isGameStarted = false;

    @Override
    protected void onCreate(Bundle bundle) {
		LogSender.startLogging(this);
        super.onCreate(bundle);
        SoLoader.init(this, false);
		ActivityManager.current = this;
		
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
		
		WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
		var windowController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
		windowController.hide(WindowInsetsCompat.Type.systemBars());
		windowController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        prefs = getSharedPreferences("Save", 0);
        if(!prefs.getBoolean("isNickSetup", false)) {
            AskUtil.ask(AskUtil.AskType.SETUP_NICK, (data) -> {
            	prefs.edit().putBoolean("isNickSetup", true).commit();
				prefs.edit().putString("nick", (String)data).commit();
				Intent intent = new Intent(this, ReactActivity.class);
				startActivity(intent);
				finish();
            });
        }
		
		if(!prefs.getBoolean("isPacksSetup", false)) {
			try {
				var stream = getAssets().open("packs/defaultList.json");
				var buffer = new byte[stream.available()];
				stream.read(buffer);
				stream.close();
				File file = new File(getExternalFilesDir(null), "packs.json");
				Files.write(file.toPath(), new String(buffer).getBytes(StandardCharsets.UTF_8));
				prefs.edit().putBoolean("isPacksSetup", true).apply();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
    }
	
	@Override
	public void onPause() {
		super.onPause();
		try {
			if(media == null || !media.isPlaying()) return;
			media.pause();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ActivityManager.current = this;
		if(isGameStarted) {
			Intent intent = new Intent(this, getClass());
			startActivity(intent);
			finish();
			return;
		}
		if(media == null) {
			media = MediaPlayer.create(this, R.raw.lobby_theme);
			media.setLooping(true);
		}
		media.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(media == null) return;
		media.stop();
		media.release();
		media = null;
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