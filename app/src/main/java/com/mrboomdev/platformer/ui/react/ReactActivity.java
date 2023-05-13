package com.mrboomdev.platformer.ui.react;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.mrboomdev.platformer.*;
import com.mrboomdev.platformer.game.pack.*;
import com.mrboomdev.platformer.online.OnlineManager;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.ZipUtil;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.time.Instant;
import java.util.List;

public class ReactActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {
    public static ReactActivity instance;
    public ReactInstanceManager reactInstance;
    public SharedPreferences prefs;
	public boolean isGameStarted;
	private long lastBackPressed;
	private ReactRootView root;

    @Override
    protected void onCreate(Bundle bundle) {
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
        if(!prefs.getBoolean("isNickSetup", false)) {
			var dialog = new AndroidDialog().setTitle("Welcome to Binacty!").setCancelable(false);
					
			dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT).setTextColor("#dddddd").setText("Please enter your nickname here."));
			var nameField = new AndroidDialog.Field(AndroidDialog.FieldType.EDIT_TEXT);
			dialog.addSpace(30).addField(nameField).addSpace(30);
					
			dialog.addAction(new AndroidDialog.Action().setText("Save and Continue").setClickListener(button -> {
				prefs.edit()
					.putBoolean("isNickSetup", true)
                    .putString("nick", nameField.getText()).apply();
					
                Intent intent = new Intent(this, ReactActivity.class);
                startActivity(intent);
                finish();
				dialog.close();
			}));
			dialog.show();
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
		super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode) {
			case 1: {
				if(resultCode != Activity.RESULT_OK || intent == null) return;
				ActivityManager.toast("Please wait. Loading the pack data", false);
				var dest = FileUtil.external("packs/temp");
				dest.remove();
				try {
					ZipUtil.unzipFile(getContentResolver().openInputStream(intent.getData()), dest, () -> {
						try {
							Moshi moshi = new Moshi.Builder().build();
							JsonAdapter<PackData.Manifest> adapter = moshi.adapter(PackData.Manifest.class);
							var pack = adapter.fromJson(dest.goTo("manifest.json").readString(false));
							if(!pack.isValid()) throw new BoomException("Error while importing a pack. Manifest file isn't valid!");
							if(!PackLoader.addPack(pack, dest)) {
								dest.getParent().goTo(pack.id).remove();
								ActivityManager.toast("This pack is already installed, trying to update...", false);
							} else {
								ActivityManager.toast("Installing a new pack...", false);
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
			
			case 2: {
				if(resultCode == Activity.RESULT_OK) {
					try {
						SignInCredential credentials = Identity.getSignInClient(this).getSignInCredentialFromIntent(intent);
						OnlineManager.getInstance().auth.signIn(credentials.getGoogleIdToken(), (result, extra) -> {
							if(result.getIsOk()) {
								if(!extra.isValid()) throw new BoomException("Invalid authentirication data!");
								prefs.edit()
									.putString("nick", credentials.getDisplayName())
									.putString("avatar", credentials.getProfilePictureUri().toString())
									.putBoolean("isSignedIn", true)
									.putString("signInMethod", "google")
									.putString("sessionToken", extra.session_token)
									.putString("player	UID", extra.player_uid)
									.apply();
									
								ActivityManager.forceExit();
							} else {
								AndroidDialog.createMessageDialog("Failed to connect the server", "Stacktrace:\n" + Log.getStackTraceString(result.getException())).show();
							}
						});
					} catch(Exception e) {
						e.printStackTrace();
						AndroidDialog.createMessageDialog("Failed to get the data", "Stacktrace:\n" + Log.getStackTraceString(e)).show();
					}
				} else {
					AndroidDialog.createMessageDialog("Failed to select the Account", "Please, try again later.").show();
				}
				break;
			}
		}
    }
}