package com.mrboomdev.platformer.ui.react.bridge;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.mrboomdev.platformer.online.Online;
import com.mrboomdev.platformer.online.profile.AuthParams;
import com.mrboomdev.platformer.online.profile.ProfileAuthentication;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.helper.BoomException;

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
	public void signIn(@NonNull String method) {
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

				ProfileAuthentication.auth(params, ActivityManager::forceExit);
				break;
			}

			default: throw new BoomException("Currently unavailable.");
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
}