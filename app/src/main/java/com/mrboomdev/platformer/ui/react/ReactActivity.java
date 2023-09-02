package com.mrboomdev.platformer.ui.react;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.game.pack.PackLoader;
import com.mrboomdev.platformer.online.OnlineManager;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.ZipUtil;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;
import java.util.Objects;

public class ReactActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {
    public static ReactActivity instance;
    public ReactInstanceManager reactInstance;
    public SharedPreferences prefs;
	public boolean isGameStarted;

	@Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SoLoader.init(this, false);
		ReactRootView root = new ReactRootView(this);

		instance = this;
		ActivityManager.current = this;
		ActivityManager.reactActivity = this;

        List<ReactPackage> packages = List.of(
				new MainReactPackage(null),
				new ReactGame()
		);

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
		ActivityManager.hideSystemUi(this);

        prefs = getSharedPreferences("Save", 0);
        if(!prefs.getBoolean("isNickSetup", false) && !prefs.getBoolean("isFirstGame", true)) {
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
		ActivityManager.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityManager.current = this;
		ActivityManager.onResume();
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
		reactInstance.onBackPressed();
    }

    @Override
	@SuppressLint("VisibleForTests")
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode) {
			case 1: {
				if(resultCode != Activity.RESULT_OK || intent == null) return;
				ActivityManager.toast("Please wait. Loading the pack data", false);
				var dest = FileUtil.external("packs/temp");
				dest.remove();
				try {
					ZipUtil.unzipFile(getContentResolver().openInputStream(Objects.requireNonNull(intent.getData())), dest, () -> {
						try {
							Moshi moshi = new Moshi.Builder().build();
							JsonAdapter<PackData.Manifest> adapter = moshi.adapter(PackData.Manifest.class);
							var pack = adapter.fromJson(dest.goTo("manifest.json").readString(false));
							if(pack == null || !pack.isValid()) throw new BoomException("Error while importing a pack. Manifest file isn't valid!");
							if(!PackLoader.addPack(dest.getParent().goTo(pack.id))) {
								ActivityManager.toast("This pack is already installed, trying to update...", false);
								dest.getParent().goTo(pack.id).remove();
							} else {
								ActivityManager.toast("Installing a new pack...", false);
							}
							dest.rename(pack.id);
							PackLoader.reloadPacks();
							PackLoader.reloadGamemodes();
							ReactContext context = reactInstance.getCurrentReactContext();
							if(context != null) {
								context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("reload", null);
							}
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
								if(!extra.isValid()) throw new BoomException("Invalid authentication data!");
								prefs.edit()
									.putString("nick", credentials.getDisplayName())
									.putString("avatar", credentials.getProfilePictureUri() != null ? credentials.getProfilePictureUri().toString() : "")
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