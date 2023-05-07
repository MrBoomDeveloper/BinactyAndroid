package com.mrboomdev.platformer.util.io;

import android.util.Log;

public class LogUtil {

	public static void debug(Tag tag, String message) {
		Log.d(tag.title, message);
	}
	
	public enum Tag {
		BOT("BotBrain"),
		ANALYTICS("Analytics"),
		PLATFORM("Platform");
		
		public String title;
		
		Tag(String title) {
			this.title = title;
		}
	}
}