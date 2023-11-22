package com.mrboomdev.platformer.util.io;

import android.util.Log;

import androidx.annotation.NonNull;

import com.mrboomdev.binacty.game.core.CoreLauncher;
import com.mrboomdev.platformer.BuildConfig;
import com.mrboomdev.platformer.game.GameAnalytics;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.ui.android.AndroidDialog;

public class LogUtil {

	public static void setThreadCrashHandler(@NonNull Thread thread) {
		thread.setUncaughtExceptionHandler((thread1, throwable) -> LogUtil.crash(throwable));
	}

	public static void crash(String title, String message, Throwable t) {
		try {
			GameAnalytics.getInstance().crash(title, message, t);
		} catch(NullPointerException ignored) {}

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
					game.launcher.exit(CoreLauncher.ExitStatus.CRASH);
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

	public static void debug(String tag, String message) {
		if(!BuildConfig.DEBUG) return;

		Log.d(tag, message);
	}

	public static void warn(String tag, String message) {
		if(!BuildConfig.DEBUG) return;

		Log.w(tag, message);
	}

	public static void error(String tag, String message) {
		if(!BuildConfig.DEBUG) return;

		Log.e(tag, message);
	}
}