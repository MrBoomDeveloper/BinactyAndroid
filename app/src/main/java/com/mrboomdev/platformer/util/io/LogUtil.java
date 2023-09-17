package com.mrboomdev.platformer.util.io;

import android.util.Log;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.ui.android.AndroidDialog;

public class LogUtil {

	public static void crash(String title, String message, Throwable t) {
		try {
			GameHolder.getInstance().analytics.crash(title, message, t);
		} catch(Exception e) {
			e.printStackTrace();
		}

		String text = title + "\n" + message + "\n" + throwableToString(t);
		FileUtil.external("crash.txt").writeString(text, false);

		var dialog = new AndroidDialog().setTitle(title).setCancelable(false);

		dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT)
				.setTextColor("#ffffff")
				.setText(message + "\n\nStacktrace:\n" + throwableToString(t))).addSpace(30);

		dialog.addAction(new AndroidDialog.Action()
				.setText("Try to restart")
				.setClickListener(button -> {
					var game = GameHolder.getInstance();
					game.launcher.exit(GameLauncher.Status.CRASH);
					dialog.close();
				}));

		dialog.addAction(new AndroidDialog.Action()
				.setText("Dismiss and continue")
				.setClickListener(button -> dialog.close()));

		dialog.show();
	}

	public static void crash(Throwable t) {
		crash("Unexpected Crash", "Something pretty strange and unknown to us has happened.", t);
	}

	@NonNull
	public static String throwableToString(Throwable t) {
		return Log.getStackTraceString(t);
	}

	public static void debug(@NonNull Tag tag, String message) {
		if(!BuildConfig.DEBUG) return;
		Log.d(tag.title, message);
	}
	
	public enum Tag {
		SHADERS("Shaders"),
		BOT("BotBrain"),
		SCRIPT_API("BinactyApi"),
		ANALYTICS("Analytics"),
		ANIMATION("Animation"),
		PLATFORM("Platform");
		
		public final String title;
		
		Tag(String title) {
			this.title = title;
		}
	}
}