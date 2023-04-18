package com.mrboomdev.platformer.ui.react;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.facebook.react.PackageList;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.soloader.SoLoader;
import com.itsaky.androidide.logsender.LogSender;
import com.mrboomdev.platformer.*;
import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.game.pack.PackLoader;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.AskUtil;
import com.mrboomdev.platformer.util.FunUtil;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.ZipUtil;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch(requestCode) {
			case 1: {
				if(resultCode != Activity.RESULT_OK || intent == null) return;
				Toast.makeText(getApplicationContext(), "Please wait. Loading the pack data", 0).show();
				var dest = FileUtil.external("packs/temp");
				dest.remove();
				try {
					ZipUtil.unzipFile(getContentResolver().openInputStream(intent.getData()), dest, () -> {
						try {
							Moshi moshi = new Moshi.Builder().build();
							JsonAdapter<PackData.Manifest> adapter = moshi.adapter(PackData.Manifest.class);
							var pack = adapter.fromJson(dest.goTo("manifest.json").readString(false));
							if(!pack.isValid()) throw new RuntimeException("Not vaild pack!");
							if(!PackLoader.addPack(pack, dest)) {
								dest.getParent().goTo(pack.id).remove();
								Toast.makeText(getApplicationContext(), "This pack is already installed, trying to update...", 0).show();
							} else {
								Toast.makeText(getApplicationContext(), "Installing a new pack...", 0).show();
							}
							dest.rename(pack.id);
							PackLoader.reloadPacks();
							PackLoader.reloadGamemodes();
							ReactContext context = reactInstance.getCurrentReactContext();
							context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("reload", null);
						} catch(Exception e) {
							e.printStackTrace();
							new AndroidDialog.SimpleBuilder("Failed to load the pack").addText("Error message: " + e.getMessage()).show();
						}
					});
				} catch(Exception e) {
					e.printStackTrace();
					new AndroidDialog.SimpleBuilder("Failed to load the pack").addText("Error message: " + e.getMessage()).show();
				}
				break;
			}
		}
    }
}