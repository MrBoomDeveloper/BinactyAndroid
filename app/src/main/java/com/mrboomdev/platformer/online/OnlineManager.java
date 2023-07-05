package com.mrboomdev.platformer.online;

import com.mrboomdev.platformer.online.profile.ProfileAuthentication;
import com.playerio.Client;

import okhttp3.OkHttpClient;

public class OnlineManager {
	private static OnlineManager instance;
	public static Client playerIOClient;
	public boolean isGuest;
	public OkHttpClient client;
	public ProfileAuthentication auth;
	
	public static OnlineManager getInstance() {
		if(instance == null) instance = new OnlineManager();
		return instance;
	}
	
	private OnlineManager() {
		this.client = new OkHttpClient();
		this.auth = new ProfileAuthentication(client);
	}
}