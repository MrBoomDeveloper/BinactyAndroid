package com.mrboomdev.platformer.script.entry;

import com.mrboomdev.platformer.game.pack.PackData;
public class JvmEntry implements ScriptEntry {
	private static final String TAG = "JvmEntry";
	private boolean isCompiled;

	@Override
	public boolean isReady() {
		return isCompiled();
	}

	@Override
	public boolean isCompiled() {
		return isCompiled;
	}

	@Override
	public void compile(PackData.GamemodeEntry entry) {
		isCompiled = true;
	}

	@Override
	public void start() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void destroy() {

	}
}