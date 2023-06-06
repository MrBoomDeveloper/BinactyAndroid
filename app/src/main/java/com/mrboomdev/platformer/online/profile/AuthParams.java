package com.mrboomdev.platformer.online.profile;

import android.app.Activity;
public class AuthParams {
	public Activity activity;
	public ConnectionId connectionId;
	public String gameId, userId;

	public AuthParams(Activity activity) {
		this.activity = activity;
	}

	public AuthParams setGameId(String id) {
		this.gameId = id;
		return this;
	}

	public AuthParams setUserId(String id) {
		this.userId = id;
		return this;
	}

	public AuthParams setConnectionId(ConnectionId id) {
		this.connectionId = id;
		return this;
	}

	public enum ConnectionId {
		PUBLIC("public");

		public final String title;

		ConnectionId(String title) {
			this.title = title;
		}
	}
}