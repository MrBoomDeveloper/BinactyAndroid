package com.mrboomdev.platformer.script.entry;

import com.mrboomdev.platformer.game.pack.PackData;

public class BeanshellEntry implements ScriptEntry {

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public boolean isCompiled() {
		return false;
	}

	@Override
	public void compile(PackData.GamemodeEntry entry) {

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