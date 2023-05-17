package com.mrboomdev.platformer.util.io;

import android.util.Log;
import androidx.annotation.NonNull;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.game.GameLauncher;
import com.mrboomdev.platformer.ui.android.AndroidDialog;

public class LogUtil {

	public static void crash(String title, String message) {
		FileUtil.external("crash.txt").writeString(title + "\n" + message, false);
		var dialog = new AndroidDialog().setTitle(title).setCancelable(false);

		dialog.addField(new AndroidDialog.Field(AndroidDialog.FieldType.TEXT)
				.setTextColor("#ffffff")
				.setText(message));

		dialog.addAction(new AndroidDialog.Action()
				.setText("Continue")
				.setClickListener(button -> {
					var game = GameHolder.getInstance();
					game.launcher.exit(GameLauncher.Status.CRASH);
					dialog.close();
				})
		).addSpace(30);

		dialog.show();
	}

	public static String throwableToString(Throwable t) {
		return Log.getStackTraceString(t);
	}

	public static void debug(@NonNull Tag tag, String message) {
		Log.d(tag.title, message);
	}
	
	public enum Tag {
		BOT("BotBrain"),
		ANALYTICS("Analytics"),
		ANIMATION("Animation"),
		PLATFORM("Platform");
		
		public final String title;
		
		Tag(String title) {
			this.title = title;
		}
	}
}