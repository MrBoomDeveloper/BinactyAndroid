package com.mrboomdev.platformer.ui.react.bridge;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.PromiseImpl;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.R;
import com.mrboomdev.platformer.online.Online;
import com.mrboomdev.platformer.online.OnlineManager;
import com.mrboomdev.platformer.online.profile.AuthParams;
import com.mrboomdev.platformer.online.profile.ProfileAuthentication;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.ui.react.ReactActivity;

import java.util.Objects;

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
			}*/

			case "name": {
				var params = new AuthParams(ActivityManager.reactActivity)
						.setConnectionId(AuthParams.ConnectionId.PUBLIC)
						.setGameId(Online.PLAYERIO_GAMEID)
						.setUserId("test_player");

				ProfileAuthentication.auth(params, () -> promise.resolve(true));
			} break;

			case "guest": {
				var params = new AuthParams(ActivityManager.reactActivity)
						.setConnectionId(AuthParams.ConnectionId.GUEST);

				ProfileAuthentication.auth(params, () -> promise.resolve(true));
			} break;

			default: {
				promise.reject("Invalid login method", "Sorry, but we didn't found anything, that can help you. Try other methods.");
			} break;
		}
	}
	
	@ReactMethod
	public void isSignedIn(@NonNull Promise promise) {
		promise.resolve(ProfileAuthentication.isLoggedIn());
	}
	
	@ReactMethod
	public void getMyData(@NonNull Promise promise) {
		WritableMap data = Arguments.createMap();
		boolean isGuest = OnlineManager.getInstance().isGuest;
		var prefs = ActivityManager.reactActivity.prefs;

		data.putString("nick", isGuest ? "Guest" : prefs.getString("nick", "Guest"));
		data.putString("avatar", isGuest ? "klarrie" : prefs.getString("avatar", "klarrie"));
		data.putInt("level", 1);
		data.putInt("progress", 0);
        promise.resolve(data);
	}

	@ReactMethod
	public void setKey(@NonNull String type, String key, Dynamic value) {
		var prefs = ActivityManager.current.getSharedPreferences("Save", 0);
		switch(type) {
			case "string":
				prefs.edit().putString(key, value.asString()).apply();
				break;

			case "int":
				prefs.edit().putInt(key, value.asInt()).apply();
				break;

			case "float":
				prefs.edit().putFloat(key, (float)value.asDouble()).apply();
				break;

			case "boolean":
				prefs.edit().putBoolean(key, value.asBoolean()).apply();
				break;
		}
	}

	@ReactMethod
	public void getKey(@NonNull String type, String key, Dynamic defaultValue, Promise promise) {
		var prefs = ActivityManager.current.getSharedPreferences("Save", 0);
		switch(type) {
			case "string":
				promise.resolve(prefs.getString(key, defaultValue.asString()));
				break;

			case "int":
				promise.resolve(prefs.getInt(key, defaultValue.asInt()));
				break;

			case "float":
				promise.resolve(prefs.getFloat(key, (float)defaultValue.asDouble()));
				break;

			case "boolean":
				promise.resolve(prefs.getBoolean(key, defaultValue.asBoolean()));
				break;
		}
	}

	@ReactMethod
	public void getKeys(@NonNull ReadableArray keys, Promise promise) {
		var prefs = ActivityManager.current.getSharedPreferences("Save", 0);
		WritableArray result = Arguments.createArray();
		for(int i = 0; i < keys.size(); i++) {
			ReadableMap was = keys.getMap(i);
			WritableMap newMap = Arguments.createMap();

			String key = was.getString("id");
			newMap.putString("id", key);

			switch(Objects.requireNonNullElse(was.getString("type"), "string")) {
				case "string":
					//TODO: Change initial to a value
					newMap.putString("initial", prefs.getString(key, was.getString("initial")));
					break;

				case "number":
					newMap.putInt("initial", prefs.getInt(key, was.getInt("initial")));
					break;

				case "boolean":
					newMap.putBoolean("initial", prefs.getBoolean(key, was.getBoolean("initial")));
					break;
			}

			result.pushMap(newMap);
		}

		promise.resolve(result);
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