package com.mrboomdev.platformer.online;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class Online {
	public static final String LOOTLOCKER_API_DOMAIN = "https://wwkna8z8.api.lootlocker.io/";
	public static final String LOOTLOCKER_API_KEY = "prod_d7a3f8fad2e04f22b36cb801a453002e";
	public static final String PLAYERIO_GAMEID = "binacty-yz2ovwcymuq5ugzvw8wa";
	
	public enum MediaType {
		JSON("application/json; charset=utf-8");
		
		private final String title;
		
		public String getTitle() {
			return title;
		}
		
		@NonNull
		@Contract(" -> new")
		public okhttp3.MediaType getMediaType() {
			return okhttp3.MediaType.get(getTitle());
		}
		
		MediaType(String title) {
			this.title = title;
		}
	}
}