package com.mrboomdev.binacty.online.lobby;

import com.mrboomdev.binacty.online.OnlineProfile;

public class LobbyAuth {
	private OnlineProfile userProfile;

	public OnlineProfile getUserProfile() {
		return userProfile;
	}

	public boolean isLoggedIn() {
		return userProfile != null;
	}

	public native void signInGoogle(String login, String password, AuthCallback callback);

	public interface AuthCallback {
		void run(boolean isSuccess, String message);
	}
}