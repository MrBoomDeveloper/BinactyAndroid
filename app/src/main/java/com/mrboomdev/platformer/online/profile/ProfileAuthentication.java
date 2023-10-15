package com.mrboomdev.platformer.online.profile;

//import static com.mrboomdev.platformer.online.OnlineManager.playerIOClient;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.online.Online;
import com.mrboomdev.platformer.online.OnlineManager;
import com.mrboomdev.platformer.online.ResultData;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileAuthentication {
	private static final OnlineManager manager = OnlineManager.getInstance();
	private final OkHttpClient client;
	
	public ProfileAuthentication(OkHttpClient client) {
		this.client = client;
	}

	public static boolean isLoggedIn() {
		if(manager.isGuest) return true;
		//return playerIOClient != null;
		return false;
	}

	public static void auth(@NonNull AuthParams params, Runnable authCallback) {
		if(Objects.equals(params.connectionId.title, "guest")) {
			manager.isGuest = true;
			authCallback.run();
			return;
		}

		var map = new HashMap<String, String>() {{
			put("userId", params.userId);
		}};

		/*PlayerIO.authenticate(
				params.activity,
				params.gameId,
				params.connectionId.title, map,
				null,
				new com.playerio.Callback<>() {
					@Override
					public void onSuccess(Client client) {
						playerIOClient = client;
						authCallback.run();
					}

					@Override
					public void onError(PlayerIOError e) {
						String message = "ErrorCode: " +
								e.errorCode.name() +
								":" +
								e.errorCode.value;

						LogUtil.crash("Failed to Authenticate", message, e);
					}
				});*/
	}
	
	public void signIn(String token, AuthCallback callback) {
		var moshi = new Moshi.Builder().build();
		var adapter = moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));
		var data = new HashMap<String, String>() {{
			put("id_token", token);
			put("game_key", Online.LOOTLOCKER_API_KEY);
			put("game_version", BuildConfig.VERSION_NAME);
			put("platform", "android");
		}};
		
		var body = RequestBody.create(adapter.toJson(data), Online.MediaType.JSON.getMediaType());
		var request = new Request.Builder()
			.url(Online.LOOTLOCKER_API_DOMAIN + "game/session/google")
			.header("Content-Type", "application/json")
			.post(body)
			.build();
		
		var call = client.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onResponse(@NonNull Call call, @NonNull Response response) {
				try {
					var moshi = new Moshi.Builder().build();
					var adapter = moshi.adapter(AuthResponse.class);
					callback.onResult(ResultData.builder().setIsOK(true).build(), adapter.fromJson(response.body().string()));
				} catch(Exception e) {
					callback.onResult(ResultData.builder().setIsOK(false).setException(e).build(), null);
				}
			}
			
			@Override
			public void onFailure(@NonNull Call call, @NonNull IOException e) {
				callback.onResult(ResultData.builder().setIsOK(false).setException(e).build(), null);
			}
		});
	}
	
	public static class AuthResponse {
		public String session_token, player_uid;
		
		public boolean isValid() {
			return true;
		}
	}
	
	public interface AuthCallback {
		void onResult(ResultData result, AuthResponse response);
	}
}