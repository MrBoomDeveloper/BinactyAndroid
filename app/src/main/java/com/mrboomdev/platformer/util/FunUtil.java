package com.mrboomdev.platformer.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class FunUtil {
	private static Array<TimerTask> timerTasks = new Array<>();
	private static Array<TimerTask> timerTasksCompleted = new Array<>();
	
	public static void update() {
		for(var task : timerTasks) {
			task.progress += Gdx.graphics.getDeltaTime();
			if(task.progress > task.delay) {
				timerTasksCompleted.add(task);
			}
		}
		for(var task : timerTasksCompleted) {
			task.runnable.run();
			timerTasks.removeValue(task, false);
		}
		timerTasksCompleted.clear();
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