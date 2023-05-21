package com.mrboomdev.platformer.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

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