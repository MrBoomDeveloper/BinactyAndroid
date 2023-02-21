package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.Gdx;
import com.mrboomdev.platformer.util.FileUtil;

public class EnvironmentCreator {
	private onCreate createHandler;
	private onException exceptionHandler;
	
	public EnvironmentCreator() {
		
	}
	
	public EnvironmentCreator setGamemode(String path, FileUtil.SourceType sourceType) {
		return this;
	}
	
	public EnvironmentCreator(onCreate createHandler, onException exceptionHandler) {
		this.createHandler = createHandler;
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public void run() {
		try {
			sleep(25);
			sleep(25);
			sleep(25);
			sleep(25);
			sleep(25);
		} catch(InterruptedException e) {
			e.printStackTrace();
			Gdx.app.postRunnable(() -> exceptionHandler.exception(e));
		}
		Gdx.app.postRunnable(() -> createHandler.create(this));
	}
	
	public interface onCreate {
		public void create(EnvironmentCreator environment);
	}
	
	public interface onException {
		public void exception(Exception e);
	}
}