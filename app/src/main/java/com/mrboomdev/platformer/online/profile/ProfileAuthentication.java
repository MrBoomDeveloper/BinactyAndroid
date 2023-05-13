package com.mrboomdev.platformer.online.profile;

import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.online.Online;
import com.mrboomdev.platformer.online.OnlineManager;
import com.mrboomdev.platformer.online.ResultData;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileAuthentication {
	private OkHttpClient client;
	
	public ProfileAuthentication(OkHttpClient client) {
		this.client = client;
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
			public void onResponse(Call call, Response response) {
				try {
					var moshi = new Moshi.Builder().build();
					var adapter = moshi.adapter(AuthResponse.class);
					callback.onResult(ResultData.builder().setIsOK(true).build(), adapter.fromJson(response.body().string()));
				} catch(Exception e) {
					callback.onResult(ResultData.builder().setIsOK(false).setException(e).build(), null);
				}
			}
			
			@Override
			public void onFailure(Call call, IOException e) {
				callback.onResult(ResultData.builder().setIsOK(false).setException(e).build(), null);
			}
		});
	}
	
	public void link(String token) {
		
	}
	
	public void refreshToken() {
		
	}
	
	public static class AuthResponse {
		public String session_token, player_name, player_uid;
		public int player_id;
		public boolean success;
		
		public boolean isValid() {
			return true;
		}
	}
	
	public interface AuthCallback {
		public void onResult(ResultData result, AuthResponse response);
	}
}