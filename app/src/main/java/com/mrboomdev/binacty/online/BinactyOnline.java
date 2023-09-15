package com.mrboomdev.binacty.online;

import com.mrboomdev.binacty.online.lobby.LobbyAuth;
public class BinactyOnline {
	public LobbyAuth auth;
	private static BinactyOnline instance;

	public native boolean init();

	public static BinactyOnline getInstance() {
		if(instance == null) {
			instance = new BinactyOnline();
		}

		return instance;
	}

	private BinactyOnline() {
		auth = new LobbyAuth();
	}
}