package com.mrboomdev.platformer.util.io;

import android.util.Log;

import androidx.annotation.NonNull;

public class LogUtil {

	public static void debug(@NonNull Tag tag, String message) {
		Log.d(tag.title, message);
	}
	
	public enum Tag {
		BOT("BotBrain"),
		ANALYTICS("Analytics"),
		ANIMATION("Animation"),
		PLATFORM("Platform");
		
		public String title;
		
		Tag(String title) {
			this.title = title;
		}
	}
}