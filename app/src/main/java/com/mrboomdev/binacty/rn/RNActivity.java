package com.mrboomdev.binacty.rn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.epicgames.mobile.eossdk.EOSSDK;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.mrboomdev.binacty.BinactyNative;
import com.mrboomdev.binacty.Constants;
import com.mrboomdev.platformer.game.pack.PackData;
import com.mrboomdev.platformer.game.pack.PackLoader;
import com.mrboomdev.platformer.online.OnlineManager;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.helper.BoomException;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.util.io.ZipUtil;

import java.util.Objects;

public class RNActivity extends ReactActivity {
	public SharedPreferences prefs;
	public boolean isGameStarted;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(null);

		BinactyNative.init();
		EOSSDK.init(getApplicationContext());

		ActivityManager.current = this;
		ActivityManager.reactActivity = this;
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

				Intent intent = new Intent(this, getClass());
				startActivity(intent);
				finish();
				dialog.close();
			}));

			dialog.show();
		}

		if(!prefs.getBoolean("isPacksListDefaultCopied", false)) {
			FileUtil.external("packs/installed.json").writeString(FileUtil.internal("packs/defaultList.json").readString(), false);
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
		System.out.println("Destroyed!");
	}

	@Override
	public void invokeDefaultOnBackPressed() {
		finishAffinity();
	}

	@Override
	@SuppressLint("VisibleForTests")
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch(requestCode) {
			case 1: {
				if(resultCode != Activity.RESULT_OK || intent == null) return;

				ActivityManager.toast("Please wait. Loading the pack data", false);
				var dest = FileUtil.external("packs/temp");
				dest.remove();

				try {
					var stream = getContentResolver().openInputStream(Objects.requireNonNull(intent.getData()));

					ZipUtil.unzipFile(stream, dest, () -> {
						try {
							var adapter = Constants.moshi.adapter(PackData.Manifest.class);
							var pack = adapter.fromJson(dest.goTo("manifest.json").readString());

							if(pack == null || !pack.isValid()) {
								throw new BoomException("Error while importing a pack. Manifest file isn't valid!");
							}

							if(!PackLoader.addPack(dest.getParent().goTo(pack.id))) {
								ActivityManager.toast("This pack is already installed, trying to update...", false);
								dest.getParent().goTo(pack.id).remove();
							} else {
								ActivityManager.toast("Installing a new pack...", false);
							}

							dest.rename(pack.id);
							PackLoader.reloadPacks();
							PackLoader.reloadGamemodes();

							ReactContext context = RNApp.getReactInstance().getCurrentReactContext();

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
								if(!extra.isValid()) {
									throw new BoomException("Invalid authentication data!");
								}

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

	@Override
	protected String getMainComponentName() {
		return "App";
	}

	@Override
	protected ReactActivityDelegate createReactActivityDelegate() {
		return new DefaultReactActivityDelegate(
				this,
				Objects.requireNonNull(getMainComponentName()),
				DefaultNewArchitectureEntryPoint.getFabricEnabled());
	}
}