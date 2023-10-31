package com.mrboomdev.platformer.util;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.mrboomdev.binacty.Constants;
import com.mrboomdev.platformer.util.helper.BoomException;

import java.io.IOException;

public class FunUtil {
	public static Array<TimerTask> timerTasks = new Array<>();
	
	public static void update() {
		var iterator = timerTasks.iterator();

		while(iterator.hasNext()) {
			var task = iterator.next();
			task.progress += Gdx.graphics.getDeltaTime();

			if(task.progress > task.delay) {
				task.runnable.run();
				iterator.remove();
			}
		}
	}

	/**
	 * Please use only for simple copy operation!
	 **/
	public static <T> T copy(Class<T> clazz, T obj) {
		var adapter = Constants.moshi.adapter(clazz);
		var json = adapter.toJson(obj);

		try {
			return adapter.fromJson(json);
		} catch(IOException e) {
			throw new BoomException(e);
		}
	}

	@NonNull
	private static String makeTwoDigitString(int value) {
		if(value > 9) {
			return "0" + value;
		}

		return String.valueOf(value);
	}

	@NonNull
	public static String formatTimer(float time, @NonNull String format) {
		float minutes = time / 60;
		float seconds = time % 60;

		return format
				.replace("ss", makeTwoDigitString((int)seconds))
				.replace("mm", makeTwoDigitString((int)minutes));
	}
	
	public static void setTimer(Runnable runnable, float delay) {
		timerTasks.add(new TimerTask(runnable, delay));
	}

	private static class TimerTask {
		public Runnable runnable;
		public float delay, progress;
		
		public TimerTask(Runnable runnable, float delay) {
			this.runnable = runnable;
			this.delay = delay;
		}
	}
}