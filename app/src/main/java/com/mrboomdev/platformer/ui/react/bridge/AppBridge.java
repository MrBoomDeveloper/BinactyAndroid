package com.mrboomdev.platformer.ui.react.bridge;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.PromiseImpl;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.R;
import com.mrboomdev.platformer.online.Online;
import com.mrboomdev.platformer.online.profile.AuthParams;
import com.mrboomdev.platformer.online.profile.ProfileAuthentication;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.ui.react.ReactActivity;

@SuppressWarnings("unused")
public class AppBridge extends ReactContextBaseJavaModule {

	public AppBridge(ReactApplicationContext context) {
		super(context);
	}

    @NonNull
	@Override
    public String getName() {
		return "AppBridge";
	}
	
	@ReactMethod
	public void signIn(@NonNull String method, Promise promise) {
		switch(method) {
			/*case "google": {
				GetSignInIntentRequest request = GetSignInIntentRequest.builder()
					.setServerClientId("125055915021-t440mrlf6u5k1lnfabh7srjblp7dertc.apps.googleusercontent.com")
					.build();

				Identity.getSignInClient(ActivityManager.current)
					.getSignInIntent(request)
					.addOnSuccessListener(result -> {
						try {
							ActivityManager.current.startIntentSenderForResult(result.getIntentSender(), 2, null, 0, 0, 0);
						} catch(Exception e) {
							e.printStackTrace();
							AndroidDialog.createMessageDialog("Failed to send Intent!", "Stacktrace:\n" + Log.getStackTraceString(e)).show();
						}
					}).addOnFailureListener(e -> {
						e.printStackTrace();
						AndroidDialog.createMessageDialog("Failed to Sign In", "Stacktrace:\n" + Log.getStackTraceString(e)).show();
					});
				break;
			}

			case "guest": {
				ActivityManager.reactActivity.prefs.edit()
						.putBoolean("isSignedIn", true)
						.putString("signedInMethod", "guest")
						.apply();

				ActivityManager.forceExit();
				break;
			}*/

			case "name": {
				var params = new AuthParams(ActivityManager.reactActivity)
						.setConnectionId(AuthParams.ConnectionId.PUBLIC)
						.setGameId(Online.PLAYERIO_GAMEID)
						.setUserId("test_player");

				ProfileAuthentication.auth(params, () -> promise.resolve(true));
				break;
			}

			case "guest":
				promise.resolve(true);
				break;

			default:
				promise.reject("Invalid login method", "Sorry, but we didn't found anything, that can help you. Try other methods.");
				break;
		}
	}
	
	@ReactMethod
	public void isSignedIn(@NonNull Promise promise) {
		promise.resolve(ProfileAuthentication.isLoggedIn());
		//promise.resolve(ActivityManager.reactActivity.prefs.getBoolean("isSignedIn", false));
	}
	
	@ReactMethod
	public void getMyData(@NonNull Promise promise) {
		var prefs = ActivityManager.reactActivity.prefs;
		WritableMap data = Arguments.createMap();
		data.putString("nick", prefs.getString("nick", "Player"));
		data.putString("avatar", prefs.getString("avatar", "klarrie"));
		data.putInt("level", 1);
		data.putInt("progress", 0);
        promise.resolve(data);
	}
	
	@ReactMethod
	public void startMusic() {
		ActivityManager.startMusic();
	}
	
	@ReactMethod
	public void stopMusic() {
		ActivityManager.stopMusic();
	}
	
	@ReactMethod
	public void setVolume(double volume) {
		ActivityManager.setVolume((float)(volume / 100));
	}

	@ReactMethod
	public void getDebug(@NonNull Promise promise) {
		var data = Arguments.createArray();
		data.pushMap(createEntry("buildType", BuildConfig.BUILD_TYPE));
		data.pushMap(createEntry("buildVersionName", BuildConfig.VERSION_NAME));
		data.pushMap(createEntry("buildVersionCode", String.valueOf(BuildConfig.VERSION_CODE)));
		data.pushMap(createEntry("applicationId", BuildConfig.APPLICATION_ID));
		data.pushMap(createEntry("brand", Build.BRAND));
		data.pushMap(createEntry("model", Build.MODEL));
		data.pushMap(createEntry("os", Build.VERSION.BASE_OS));
		promise.resolve(data);
	}

	@NonNull
	private ReadableMap createEntry(String key, String value) {
		var map = Arguments.createMap();
		map.putString("key", key);
		map.putString("value", value);
		return map;
	}

	@ReactMethod
	public void restart() {
		var activity = ActivityManager.reactActivity;
		var props = Arguments.createMap();

		props.putString("title", activity.getString(R.string.dialog_restart_title));
		props.putString("description", activity.getString(R.string.dialog_restart_description));
		props.putString("ok", activity.getString(R.string.action_restart));
		showDialog(props, new PromiseImpl(args -> {
			stopMusic();
			var intent = new Intent(activity, ReactActivity.class);
			activity.startActivity(intent);
			activity.finish();
		}, args -> {}));
	}

	@ReactMethod
	public void exit() {
		var activity = ActivityManager.reactActivity;
		var props = Arguments.createMap();

		props.putString("title", activity.getString(R.string.dialog_exit_title));
		props.putString("description", activity.getString(R.string.dialog_exit_description));
		props.putString("ok", activity.getString(R.string.action_exit));
		showDialog(props, new PromiseImpl(args -> {
			activity.finishAffinity();
			stopMusic();
		}, args -> {}));
	}

	@ReactMethod
	public void showDialog(@NonNull ReadableMap props, Promise promise) {
		var activity = ActivityManager.reactActivity;
		var dialog = new AndroidDialog().setTitle(props.getString("title"));

		dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT)
				.setTextColor("#ffffff")
				.setText(props.getString("description")));

		dialog.addAction(new AndroidDialog.Action()
				.setText(props.hasKey("ok") ? props.getString("ok") : activity.getString(R.string.action_continue))
				.setClickListener(button -> {
					promise.resolve(null);
					dialog.close();
				}));

		dialog.addAction(new AndroidDialog.Action()
				.setText(props.hasKey("cancel") ? props.getString("cancel") : activity.getString(R.string.action_cancel))
				.setClickListener(button -> {
					dialog.close();
					promise.reject("Dialog rejected", "Cancel button was pressed");
				}));

		dialog.addSpace(30).show();
	}
}